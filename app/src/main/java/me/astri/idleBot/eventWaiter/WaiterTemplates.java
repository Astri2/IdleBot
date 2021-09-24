package me.astri.idleBot.eventWaiter;

import me.astri.idleBot.main.Lang;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class WaiterTemplates {
    private static Collection<ActionRow> getKP(String id, boolean canValidate, boolean allDisabled) {
        return Arrays.asList(
                ActionRow.of(Button.secondary(id + "7", "7").withDisabled(allDisabled), Button.secondary(id + "8", "8").withDisabled(allDisabled), Button.secondary(id + "9", "9").withDisabled(allDisabled)),
                ActionRow.of(Button.secondary(id + "4", "4").withDisabled(allDisabled), Button.secondary(id + "5", "5").withDisabled(allDisabled), Button.secondary(id + "6", "6").withDisabled(allDisabled)),
                ActionRow.of(Button.secondary(id + "1", "1").withDisabled(allDisabled), Button.secondary(id + "2", "2").withDisabled(allDisabled), Button.secondary(id + "3", "3").withDisabled(allDisabled)),
                ActionRow.of(Button.secondary(id + "0","0").withDisabled(allDisabled),Button.danger(id + "delete","✖").withDisabled(!canValidate),
                        Button.success(id + "done","✔️").withDisabled(!canValidate)));
    }

    public static void numPadEvent(String message, InteractionHook hook, boolean isEphemeral, AtomicReference<String> number, Lang lang,@Nullable Runnable postExecutionAction) {
        String id = System.currentTimeMillis() + hook.getInteraction().getUser().getId() + "_";
        number.set("");
        hook.sendMessage(message + "\n" + number + "_").setEphemeral(isEphemeral)
                .addActionRows(getKP(id,false, false))
                .queue(msg -> {
                    Waiter<ButtonClickEvent> waiter = new Waiter<>();
                    waiter.setEventType(ButtonClickEvent.class)
                            .setExpirationTime(1, TimeUnit.MINUTES)
                            .setTimeoutAction(() -> {
                                msg.editMessage(message + "\n" + lang.get("expired")).queue();
                                msg.editMessageComponents(getKP(id,false,true)).queue();
                            })
                            .setConditions(e ->
                                e.getInteraction().getUser().equals(hook.getInteraction().getUser()) && e.getInteraction().getButton().getId().startsWith(id)
                                    && e.getMessageId().equals(msg.getId())
                            )
                            .setFailureAction(ctx -> {
                                if(ctx.getEvent().getMessageId().equals(msg.getId()))
                                    ctx.getEvent().reply(lang.get("error_cant_interact",ctx.getEvent().getUser().getAsMention()))
                                            .setEphemeral(true).queue();
                            })
                            .setAction(ctx -> {
                                String suffix = ctx.getEvent().getInteraction().getButton().getId().substring(id.length()); //get le suffix to know which button pressed
                                if(Character.isDigit(suffix.toCharArray()[0])) {
                                    number.set(number.get()+suffix);
                                    ctx.getEvent().editMessage(message + "\n" + number.get() + "_").queue();
                                    ctx.getEvent().getHook().editOriginalComponents(getKP(id,true,false)).queue();
                                }
                                else if(suffix.equals("delete")) {
                                    number.set("");
                                    ctx.getEvent().editMessage(message + "\n" + number.get() + "_").queue();
                                    ctx.getEvent().getHook().editOriginalComponents(getKP(id,false,false)).queue();
                                }
                                else if(suffix.equals("done")) {
                                    ctx.getEvent().editMessage(number.get().replace("_","")).queue();
                                    ctx.getEvent().getHook().editOriginalComponents(getKP(id,false,true)).queue();
                                    ctx.unregister();
                                    postExecutionAction.run();
                                }
                            }).register("KP_" + hook.getInteraction().getUser().getId());
                });
    }
}
