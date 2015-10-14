package bmckalip.WelcomeRewarder;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class WelcomeRewarder extends JavaPlugin implements Listener{

public Player newPlayer;
private String[][] welcomedList = new String[2][50]; 
private int arrayLocation;
private String pluginPrefix = ChatColor.WHITE + "[" + ChatColor.RED + "Welcome Rewarder" + ChatColor.WHITE + "]"; 
int rewardAmount = getConfig().getInt("rewardAmount");

	@EventHandler
	public Player getNewPlayer(PlayerJoinEvent event){
		newPlayer = event.getPlayer();
		return newPlayer;
	}
	
	@EventHandler
	public void reward(final AsyncPlayerChatEvent event){
		//Changeable variables
		double minimumPercentage = getConfig().getInt("minimumPercentage");
		
		//unchangeable variables
		Player sender = event.getPlayer();
		String newPlayerName = newPlayer.getPlayerListName();
		String senderName = sender.getPlayerListName();
		String message = event.getMessage();
		int newPlayerNameLength = newPlayerName.length();
		double minimumPartitionSize = Math.floor((minimumPercentage / 100) * newPlayerNameLength);
		String newPlayerNamePartitions[] = new String[newPlayerNameLength - (int)minimumPartitionSize + 1];
		if((message.toLowerCase().contains("welcome") || 
				message.toLowerCase().contains("welcom") || 
				message.toLowerCase().contains("welcoem") || 
				message.toLowerCase().contains("welcum") ||
				message.toLowerCase().contains("wellcome"))){
		if(!getWelcomedStatus(newPlayerName, senderName)){
			if(!newPlayer.hasPlayedBefore() && newPlayerName != senderName){
				//player name list
				for(int i = 0; i <= newPlayerNameLength - minimumPartitionSize; i++){
					newPlayerNamePartitions[i] = newPlayerName.substring(i, i + (int)minimumPartitionSize); 
				}
				//getLogger().info("partitionsize: " + minimumPartitionSize);
				//getLogger().info("# of partitions: " + newPlayerNamePartitions.length);
				for(int i = 0; i <= newPlayerNamePartitions.length-1; i++){
					//getLogger().info(i + ": " + newPlayerNamePartitions[i]);
					if(message.toLowerCase().contains(newPlayerNamePartitions[i])){
						sender.sendMessage(pluginPrefix + " Thanks for welcoming " + newPlayerName + " to the server! $" + rewardAmount + " has been added to your balance.");
						
						if(rarePrize()){
							awardRarePrize(sender);
							sender.sendMessage("You've won a rare prize!");
						}
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + senderName + " " + rewardAmount);
						
						setWelcomedStatus(newPlayerName, senderName);
						break;
					}
				}
			}
		}
	}
	}
	
	public void onEnable(){
		getConfig().options().copyDefaults(true);
		saveConfig();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	public boolean getWelcomedStatus(String newPlayer, String sender){
		boolean welcomedStatus = false;
		for(int i = 0; i <= arrayLocation-1; i++){
			if(welcomedList[0][i] == newPlayer && welcomedList[1][i] == sender){
				welcomedStatus = true;
			}
		}
		return welcomedStatus;
	}

	public void setWelcomedStatus(String joiner, String welcomer){
		//getLogger().info("entered setWelcomedStatus");
		welcomedList[0][arrayLocation] = joiner;
		welcomedList[1][arrayLocation] = welcomer;
		arrayLocation++;
	}
	
	public boolean rarePrize(){
		int rarity = getConfig().getInt("rarity");
		boolean rareStatus = false;
		Random rand = new Random();
		
		if(rand.nextInt(100) >= (100 - rarity)){
			rareStatus = true;
		}
		return rareStatus;
	}
	
	public void awardRarePrize(Player player){
		Random rand = new Random();
		int rewardCount = rand.nextInt(8) + 1;
		ItemStack[] reward = new ItemStack[6];
		int j = rand.nextInt(reward.length-1);
		
		reward[0] = new ItemStack(Material.DIAMOND);
		reward[1] = new ItemStack(Material.EMERALD);
		reward[2] = new ItemStack(Material.GOLD_INGOT);
		reward[3] = new ItemStack(Material.IRON_INGOT);
		reward[4] = new ItemStack(Material.COAL);
		reward[5] = new ItemStack(Material.REDSTONE);
		
		PlayerInventory playerInventory = player.getInventory();
		
		for(int i = 0; i < rewardCount; i++){
			playerInventory.addItem(reward[j]);
		}
	}
}




