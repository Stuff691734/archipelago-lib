package net.stuff691734.archipelagoLib.archipelagoClient;

import com.google.gson.JsonObject;
import io.github.archipelagomw.Client;
import io.github.archipelagomw.ClientStatus;
import io.github.archipelagomw.bounce.DeathLinkHandler;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ConnectionResultEvent;
import io.github.archipelagomw.events.PrintJSONEvent;
import io.github.archipelagomw.flags.ItemsHandling;
import io.github.archipelagomw.network.ConnectionResult;
import net.stuff691734.archipelagoLib.CheckType;
import net.stuff691734.archipelagoLib.Logic;
import net.stuff691734.archipelagoLib.SlotData;
import net.stuff691734.archipelagoLib.interfaces.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArchipelagoClient extends Client {
    final UtilsInterface utils;
    final ServerInterface server;
    final ServerStorageInterface state;

    public ArchipelagoClient(UtilsInterface utils, ServerInterface server, ServerStorageInterface state) {
        super();
        this.utils = utils;
        this.server = server;
        this.state = state;
        this.getEventManager().registerListener(this);

        this.setGame("Modded Minecraft");
        this.setItemsHandlingFlags(ItemsHandling.SEND_STARTING_INVENTORY | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_ITEMS);
    }

    @Override
    public void onError(Exception ex) {
        utils.logError(ex.getLocalizedMessage());
        utils.sendMessageTranslatable("archipelago.client.error", ex.getMessage());
    }

    @Override
    public void onClose(String Reason, int attemptingReconnect) {
        utils.logInfo(Reason);
    }

    @ArchipelagoEventListener
    public void onDeathLink(io.github.archipelagomw.events.DeathLinkEvent event) {
        utils.sendMessageTranslatable("archipelago.death_link.info", event.source, event.cause);
        server.execute(server::killPlayers);
    }

    @ArchipelagoEventListener
    public void onEvent(PrintJSONEvent event) {
        utils.sendMessageTranslatable("archipelago.client.message", event.apPrint.getPlainText());
    }

    @ArchipelagoEventListener
    public void onConnection(ConnectionResultEvent event) {
        JsonObject data = event.getSlotData(JsonObject.class);
        if (event.getResult() != ConnectionResult.Success) {
            utils.sendMessageTranslatable("archipelago.connection.refused", event.getResult().name());
            return;
        }

        Map<String, String> stateSlotData = this.state.getSlotData();
        data.entrySet().forEach((entry) -> stateSlotData.put(entry.getKey(), entry.getValue().getAsString()));
        SlotData slotData = new SlotData(stateSlotData);

        this.server.setSlotData(slotData);
        this.server.sendSlotDataPacket(stateSlotData);
        this.server.sendChecksDataPacket(new ArrayList<>(this.state.getChecks().keySet()));
        this.server.updateLogic();

        if (slotData.death_link) {
            utils.logInfo("DeathLink activated");
            this.setDeathLinkEnabled(true);
            this.addTag("DeathLink");
        }
        utils.logInfo(stateSlotData.toString());

        this.state.getPendingChecks().forEach(this::sendCheck);

        // handled, remove so they aren't given again
        this.state.getPendingChecks().clear();
        this.state.setDirty();
    }

    @ArchipelagoEventListener
    public void onReceiveItems(io.github.archipelagomw.events.ReceiveItemEvent event) {
        utils.sendMessageTranslatable(
                "archipelago.client.received_check",
                event.getItemName(),
                event.getPlayerName(),
                event.getLocationName()
        );
        String[] itemName = event.getItemName().split(" ",2);

        this.parseItem(itemName[0], itemName[1], event.getIndex());
    }

    public void parseItem(String itemType, String itemName, Long index) {
        CheckType checkType = CheckType.getCheckType(itemType);
        switch (checkType) {
            case ADVANCEMENT:
                String advancementName = itemName.split(" ",2)[0];
                if (utils.isAdvancementId(advancementName)) {
                    this.state.addCheck(checkType.addPrefix(advancementName));
                    AdvancementInterface advancement = this.server.getAdvancement(advancementName);
                    this.server.sendCheckPacket(checkType.addPrefix(advancementName));
                    advancement.updateVisibility();

                    SlotData slotData = this.server.getSlotData();

                    if (slotData.isInitiated && slotData.advancement_checks_give_items) {
                        if (advancement.hasDisplay()) {
                            utils.giveItem(this.server, advancement, index);
                        }
                    }
                }
                break;
            case FTB_QUEST:
                String questName = itemName.split(" ",2)[0];
                if (this.server.isModLoaded("ftbquests") && utils.isQuestId(questName)) {
                    this.state.addCheck(checkType.addPrefix(questName));
                    this.server.sendCheckPacket(checkType.addPrefix(questName));

                    this.server.getFTBQuest(questName).ifPresent(FTBQuestsInterface::updateVisibility);
                }
                break;
            case BETTER_QUESTING:
                String betterQuestingQuestName = itemName.split(" ",2)[0];
                if (this.server.isModLoaded("betterquesting") && utils.isBetterQuestingQuestId(betterQuestingQuestName)) {
                    this.state.addCheck(checkType.addPrefix(betterQuestingQuestName));
                    this.server.sendCheckPacket(checkType.addPrefix(betterQuestingQuestName));

                    this.server.getBetterQuestingQuest(betterQuestingQuestName).ifPresent(BetterQuestingInterface::updateVisibility);
                }
                break;
            case ITEM:
                if (utils.isItemId(itemName)) {
                    utils.giveItem(this.server, itemName, index);
                }
                break;
            case DEFAULT:
                utils.logInfo(String.format(
                        "Received item: '%s' with type signature: '%s'. it did not match any known types",
                        itemName, itemType
                ));
                break;
        }
        if (index != null) {
            this.state.updateLastCheck(index);
        }
        this.state.setDirty();
    }

    /**
     * Returns a list of all locations that match the given name.
     * @param locationName the name to check against the location names.
     * @return a lost of location ids.
     */
    private List<Long> getLocationId(String locationName) {
        return this.getDataPackage().getGame("Modded Minecraft")
                .locationNameToId.keySet().stream()
                .filter(
                        (key) -> locationName.equals(String.format("%s %s", (Object[]) key.split(" ")))
                ).map(
                        (value) -> this.getDataPackage().getGame("Modded Minecraft").locationNameToId.get(value)
                ).collect(Collectors.toList());
    }

    /**
     * Sends a check based on the check name.
     * @param checkName the name of the check to send.
     */
    public void sendCheck(String checkName) {
        if (this.isConnected()) {
            for (Long check_id : getLocationId(checkName)) {
                this.getLocationManager().checkLocation(check_id);
                if (this.server.getSlotData().isCheckFinalGoal(checkName)) {
                    this.setGameState(ClientStatus.CLIENT_GOAL);
                }
            }
        } else {
            this.state.addPendingCheck(checkName);
            this.state.setDirty();
        }
    }

    public void sendDeathLink(String message) {
        if (this.getTags().contains(DeathLinkHandler.DEATHLINK_TAG)) {
            this.sendDeathlink(this.getMyName(), message);
        }
    }

    public boolean isValidId(CheckType type, String id) {
        switch (type) {
            case FTB_QUEST:
                return this.utils.isQuestId(id);
            case ADVANCEMENT:
                return this.utils.isAdvancementId(id);
            case BETTER_QUESTING:
                return this.utils.isBetterQuestingQuestId(id);
            case ITEM:
                return this.utils.isItemId(id);
            default:
                return false;
        }
    }

    public int connectCommand(ContextInterface context, String name, String address) {
        this.setName(name);
        try {
            this.connect(address);
        } catch (URISyntaxException e) {
            context.sendMessageTranslatable("archipelago.connection.invalid_server", address);
            return 1;
        }
        return 0;
    }

    public int setPasswordCommand(ContextInterface context, String password) {
        this.setPassword(password);
        context.sendMessageTranslatable("archipelago.command.password.success");
        return 0;
    }

    public int getCommand(ContextInterface context) {
        context.sendMessageTranslatable("archipelago.command.get.checks", this.state.getChecks().toString());
        context.sendMessageTranslatable("archipelago.command.get.slotData", this.state.getSlotData().toString());
        return 0;
    }

    public int getSpecificCommand(ContextInterface context, String check) {
        String[] checkParts = check.split(" ", 3);
        context.sendMessageTranslatable(
                "archipelago.command.get.specific",
                String.format("%s (%s)", (Object[]) checkParts),
                String.valueOf(state.hasCheck(String.format("%s (%s)", (Object[]) checkParts)))
        );
        return 0;
    }

    public int generateCommand(Logic logic, ContextInterface context, boolean singleLine, boolean removePermaHidden) {
        return logic.generateChecks(this.server, context, singleLine, removePermaHidden);
    }
}
