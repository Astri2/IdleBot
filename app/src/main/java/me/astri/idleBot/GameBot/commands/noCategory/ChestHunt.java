package me.astri.idleBot.GameBot.commands.noCategory;

import me.astri.idleBot.GameBot.entities.BigNumber;
import me.astri.idleBot.GameBot.entities.PlayerChestHunt;
import me.astri.idleBot.GameBot.entities.player.Player;
import me.astri.idleBot.GameBot.eventWaiter.Waiter;
import me.astri.idleBot.GameBot.game.GameUtils;
import me.astri.idleBot.GameBot.slashCommandHandler.ISlashCommand;
import me.astri.idleBot.GameBot.utils.Emotes;
import me.astri.idleBot.GameBot.utils.Lang;
import me.astri.idleBot.GameBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ChestHunt extends ISlashCommand {
    @Override
    public BaseCommand<CommandData> getData() {
        return new CommandData("chesthunt","player a real useless chesthunt")
                .addOption(OptionType.BOOLEAN,"just4fun","Is this chest just for the fun of chesthunt or should it count towards progression",false);
    }

    @Override
    public void handle(SlashCommandEvent e, InteractionHook hook) {
        OptionMapping option = e.getOption("just4fun");
        boolean realHunt = (option == null || !option.getAsBoolean());

        Player p = GameUtils.getUser(hook,e.getUser());
        if(p == null) return;
        PlayerChestHunt ch = p.getChestHunt();

        if(!ch.isUnlocked()) {
            hook.sendMessage(p.getLang().get("chest_hunt_locked",e.getUser().getAsMention())).queue();
            return;
        }

        String streak_msg = "";
        if(realHunt) {
            //1h cooldown
            long cooldown = (ch.getLastTime() + 3600000) - System.currentTimeMillis();
            if (cooldown > 0) {
                hook.sendMessage(p.getLang().get("chesthunt_cooldown", e.getUser().getAsMention(), Utils.timeParser(cooldown, TimeUnit.MILLISECONDS))).queue();
                return;
            }

            //you then loose 1 streak level per hour
            int old = ch.getStreak();
            if (cooldown < -3600000) {
                int newStreak = Math.max(1,old - (int)(cooldown/-3600000));
                ch.setStreak(newStreak);
                streak_msg = p.getLang().get("chest_hunt_streak_lost",Integer.toString(old),Integer.toString(newStreak));
            } else {
                ch.setStreak(ch.getStreak() + 1);
                streak_msg = p.getLang().get("chest_hunt_streak",Integer.toString(old),Integer.toString(ch.getStreak()));
            }
            ch.updateLastTime();
        }

        int[][] grid = getGrid();
        String multiplier = Integer.toString(Math.min(10,ch.getStreak()));
        EmbedBuilder eb = getEmbed(p,e.getUser(), realHunt, new BigNumber(),multiplier,"", null);
        if(realHunt)
            eb.setFooter(streak_msg);
        hook.sendMessageEmbeds(eb.build()).addActionRows(getRows(grid, false)).queue(msg ->
            new Waiter<>(getWaiter(p, e.getUser(), realHunt, msg, grid, ch.getStreak())).register("chestHunt_"+e.getUser().getId())
        );
    }

    private static final String[] emotes = {"<:closed_chest:946562343645626469>","<:coin:883817946663747614>","<:slayer_point:882795259971661844>",
            "<:mimic:946563866110853140>","<:Knight_Shield:884894974552977419>"};
    private ArrayList<ActionRow> getRows(int[][] grid, boolean allDisable) {
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
        int x,y;

        while(mimicPlaced < 3) {
            x = (int) Math.floor(Math.random() * 5);
            y = (int) Math.floor(Math.random() * 5);
            if(grid[x][y] == 0) {
                grid[x][y] = -3;
                mimicPlaced++;
            }
        }
        while(shieldPlaced < 1) {
            x = (int) Math.floor(Math.random() * 5);
            y = (int) Math.floor(Math.random() * 5);
            if(grid[x][y] == 0) {
                grid[x][y] = -4;
                shieldPlaced++;
            }
        }
        for(int i = 0 ; i < 5 ; i++) for(int k = 0 ; k < 5 ; k++) {
            if (grid[i][k] == 0)
                grid[i][k] = -1;
        }
        //.out.println(Arrays.deepToString(grid));
        return grid;
    }

    private Waiter<ButtonClickEvent> getWaiter(Player p, User user, boolean realHunt, Message msg, int[][] grid, int streak) {
        AtomicInteger saver = new AtomicInteger(0);
        BigNumber gain = new BigNumber();
        final BigNumber prod = p.getProduction();
        final BigNumber CPS = prod.toDouble() == 0 ? new BigNumber(1) : prod;
        final BigNumber multiplier = new BigNumber(Math.min(streak,10));
        AtomicInteger remainingRewardChests = new AtomicInteger(21);
        AtomicInteger crystalSavers = new AtomicInteger(1);

        return new Waiter<ButtonClickEvent>()
                .setEventType(ButtonClickEvent.class)
                .setAutoRemove(false)
                .setExpirationTime(5L, TimeUnit.MINUTES)
                .setConditions(ctx -> ctx.getUser().getId().equals(p.getId()) && ctx.getMessage().equals(msg))
                .setTimeoutAction(() -> msg.editMessage("expired").setActionRows(getRows(grid, true)).queue())
                .setFailureAction(ctx -> {
                    if(ctx.getEvent().getMessage().equals(msg))
                        ctx.getEvent().reply("you can't interact with that!").setEphemeral(true).queue();
                })
                .setAction(ctx -> {
                    ctx.resetTimer();

                    String[] pos = ctx.getEvent().getButton().getId().split(" ");
                    int val = grid[Integer.parseInt(pos[0])][Integer.parseInt(pos[1])];
                    boolean allDisabled = false;
                    EmbedBuilder eb = new EmbedBuilder();
                    switch(val) {
                        case -1 -> {
                            BigNumber reward = BigNumber.multiply(CPS,new BigNumber(30 + Math.random() * 90));
                            reward.multiply(multiplier);
                            gain.add(reward);
                            eb = getEmbed(p, user, realHunt, gain, multiplier.getUnitNotation(), "chest_hunt_coins",
                                new String[]{reward.getNotation(p.usesScNotation()) + " " + Emotes.get("coin") + " " + p.getLang().get("coins")});
                            if(realHunt)
                                p.editCoins(reward);
                            remainingRewardChests.decrementAndGet();
                            if(remainingRewardChests.get() == 0) {
                                eb.setFooter(p.getLang().get("chest_hunt_win"));
                                allDisabled = true;
                                ctx.unregister();
                            }
                        }
                        case -2 -> { //sp, not implemented yet
                        }
                        case -3 -> {
                            if(crystalSavers.get() > 0) {
                                eb = getEmbed(p,user, realHunt, gain,multiplier.getUnitNotation(),"chest_hunt_mimic_crystal",
                                        new String[]{Emotes.get("crystal_saver")});
                            }
                            else if(saver.get() > 0) {
                                saver.decrementAndGet();
                                eb = getEmbed(p,user, realHunt, gain,multiplier.getUnitNotation(),"chest_hunt_mimic_saved",
                                        new String[]{Emotes.get("saver") + " " + saver.get()});
                            } else {
                                eb = getEmbed(p, user, realHunt, gain, multiplier.getUnitNotation(), "chest_hunt_mimic", new String[]{});
                                allDisabled = true;
                                ctx.unregister();
                            }
                        }
                        case -4 -> {
                            saver.incrementAndGet();
                            eb = getEmbed(p,user, realHunt, gain,multiplier.getUnitNotation(),"chest_hunt_saver",
                                    new String[]{Emotes.get("saver") + " " + saver.get()});
                        }
                    }
                    grid[Integer.parseInt(pos[0])][Integer.parseInt(pos[1])] *= -1;
                    crystalSavers.decrementAndGet();
                    ctx.getEvent().editMessageEmbeds(eb.build()).setActionRows(getRows(grid,allDisabled)).queue();
                });
    }

    private EmbedBuilder getEmbed(Player p, User user, boolean realHunt, BigNumber gains, String multiplier, String eventId, @Nullable String[] args) {
        Lang l = p.getLang();
        EmbedBuilder eb = new EmbedBuilder().setAuthor(l.get(realHunt ? "chest_hunt" : "chest_hunt_fun",user.getName()),null,user.getAvatarUrl())
                        .setColor(p.getColor());
        eb.setDescription(l.get("chest_hunt_multiplier",multiplier) + "\n");
        eb.appendDescription(l.get("chest_hunt_reward", gains.getNotation(p.usesScNotation()) + Emotes.get("coin") + " " + l.get("coins")));
        if(!eventId.isEmpty())
            eb.setTitle(l.get(eventId,args));
        return eb;
    }
}
