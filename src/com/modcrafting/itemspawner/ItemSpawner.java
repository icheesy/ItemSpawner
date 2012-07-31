package com.modcrafting.itemspawner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemSpawner extends JavaPlugin{
	public void onEnable(){
		createDefaultConfiguration("config.yml");
	}
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
		boolean auth = false;
		if(sender instanceof Player){
			if(sender.hasPermission("itemspawner.command") || ((Player) sender).isOp()) auth = true;
		}else{
			auth = true;		
		}
		if(!auth){
			sender.sendMessage(ChatColor.RED+"You do not have the required permissions.");
			return true;
		}
		int mat = 0;
		int qty = 1;
		int dam = 0;
		String alias = null;
		if(args.length<1) return false;
		if(args.length>1){
			try{
				qty = Integer.parseInt(args[1].trim());
			}catch(NumberFormatException nfe){
				return false;
			}
		}
		if(args[0].contains(":")){
			String[] comb = args[0].split(":");
			String m = comb[0].replaceAll(":", "").trim();
			String d = comb[1].replaceAll(":", "").trim();
			try{
				mat = Integer.parseInt(m);
				dam = Integer.parseInt(d);
			}catch(NumberFormatException nfe){
				return false;
			}
		}else{
			try{
				mat = Integer.parseInt(args[0]);
			}catch(NumberFormatException nfe){
				alias = args[0].toLowerCase();
			}
		}
		YamlConfiguration config = (YamlConfiguration) this.getConfig();
		if(alias!=null) mat = config.getInt("Item."+alias+".id", 0);
		if(alias!=null) dam = config.getInt("Item."+alias+".damage", 0);
		List<Integer> blacklist = config.getIntegerList("BlackList");
		if(mat!=0&&!blacklist.contains(mat)){
			ItemStack item = null;
			if(dam!=0){
				item = new ItemStack(mat, qty, Short.parseShort(String.valueOf(dam)));
			}else{
				item = new ItemStack(mat, qty);
			}
			if(sender instanceof Player){
				Player player = (Player) sender;
				if(item!=null)player.getInventory().addItem(item);
				player.sendMessage(ChatColor.BLUE+"Recieved Item:"+item.getType().toString()+" Qty:"+String.valueOf(qty));
				return true;
			}
		}
		if(blacklist.contains(mat)){
			sender.sendMessage(ChatColor.RED+"This item is blacklisted!");
			return true;
		}
		return false;
	}
	public void createDefaultConfiguration(String name) {
		new File("plugins/ItemSpawner/").mkdir();
		File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {

			InputStream input =
				this.getClass().getResourceAsStream("/" + name);
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}

					System.out.println(getDescription().getName()
							+ ": Default configuration file written: " + name);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (IOException e) {}

					try {
						if (output != null)
							output.close();
					} catch (IOException e) {}
				}
			}
		}
	}
}
