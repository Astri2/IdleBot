package me.astri.idleBot.GameBot.commands.noCategory;

import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Chesthunt extends ISlashCommand {
    @Override
    public BaseCommand<CommandData> getData() {
        return new CommandData("chesthunt","player a real useless chesthunt");
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {

        int[][] grid = getGrid();
        hook.sendMessage("gl").addActionRows(getRows(grid, false)).queue(msg ->
            new Waiter<>(getWaiter(e.getUser(), msg, grid)).register("chestHunt_"+e.getUser().getId())
        );
    }

    private static final String[] emotes = {"<:closed_chest:946562343645626469>","<:coin:883817946663747614>","<:slayer_point:882795259971661844>",
            "<:mimic:946563866110853140>","<:Knight_Shield:884894974552977419>"};
    private ArrayList<ActionRow> getRows(int[][] grid, boolean allDisable) {
        System.out.println(Arrays.deepToString(grid));

        ArrayList<ActionRow> rows = new ArrayList<>();
        for(int i = 0 ; i < 5 ; i++) {
            ArrayList<Button> buttons = new ArrayList<>();
            for(int k = 0 ; k < 5 ; k++) {
                int val = Math.max(grid[i][k],0);
                ButtonStyle style;
                switch(val) {
                    case 1,2 -> style = ButtonStyle.PRIMARY;
                    case 3 -> style = ButtonStyle.DANGER;
                    case 4 -> style = ButtonStyle.SUCCESS;
                    default -> style = ButtonStyle.SECONDARY;
                }
                buttons.add(Button.of(style,i + " " + k, Emoji.fromMarkdown(emotes[val])).withDisabled(val != 0 || allDisable));
            }
            rows.add(ActionRow.of(buttons));
        }
        return rows;
    }

    private int[][] getGrid() {
        int[][] grid = new int[5][5];
        for(int i = 0 ; i < 5 ; i++) for(int k = 0 ; k < 5 ; k++) grid[i][k]=0;
        int mimicPlaced = 0, shieldPlaced = 0;
        int x=0,y=0;

        while(mimicPlaced < 3) {
            x = (int) Math.floor(Math.random() * 5);
            y = (int) Math.floor(Math.random() * 5);
            if(grid[x][y] != -3) {
                grid[x][y] = -3;
                mimicPlaced++;
            }
        }
        while(shieldPlaced < 1) {
            x = (int) Math.floor(Math.random() * 5);
            y = (int) Math.floor(Math.random() * 5);
            if(grid[x][y] != -4) {
                grid[x][y] = -4;
                shieldPlaced++;
            }
        }
        for(int i = 0 ; i < 5 ; i++) for(int k = 0 ; k < 5 ; k++) {
            if (grid[i][k] != 0) continue;
            grid[i][k] = (Math.random() < 0.5) ? -1 : -2;
        }
        return grid;
    }

    private Waiter<ButtonClickEvent> getWaiter(User user, Message msg, int[][] grid) {
        AtomicInteger saver = new AtomicInteger(0);
        return new Waiter<ButtonClickEvent>()
                .setEventType(ButtonClickEvent.class)
                .setAutoRemove(false)
                .setExpirationTime(5L, TimeUnit.MINUTES)
                .setConditions(ctx -> ctx.getUser().equals(user) && ctx.getMessage().equals(msg))
                .setTimeoutAction(() -> msg.editMessage("expired").setActionRows(getRows(grid,true)).queue())
                .setFailureAction(a -> {
                    if(a.getEvent().getMessage().equals(msg))
                        a.getEvent().reply("you can't interact with that!").setEphemeral(true).queue();
                })
                .setAction(a -> {
                    System.out.println(a.getEvent().getButton().getId());
                    a.resetTimer();

                    String[] pos = a.getEvent().getButton().getId().split(" ");
                    int val = grid[Integer.parseInt(pos[0])][Integer.parseInt(pos[1])];
                    boolean lost = false;
                    if(val == -4) {//saver
                        saver.incrementAndGet();
                    } else if(val == -3) { //mimic
                        if(saver.get() > 0) saver.decrementAndGet();
                        else lost = true;
                    }
                    grid[Integer.parseInt(pos[0])][Integer.parseInt(pos[1])] *= -1;

                    a.getEvent().getHook().editOriginalComponents(getRows(grid,lost)).queue();
                    a.getEvent().editMessage("gl").queue();
                });
    }
}
