package com.chumcraft.usefulwanderingtrader;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerHeads {

    public ArrayList<ItemStack> PlayerHeadList = new ArrayList<ItemStack>();
    public ArrayList<String> playernames = new ArrayList<String>();
    private UWTPlugin plugin;

    public PlayerHeads(UWTPlugin plugin) 
    {
        this.plugin = plugin;
        updateHeadList();
    }

    private void updateHeadList(OfflinePlayer[] playerlist)
    {
        int stacksize = this.plugin.getConfiguration().getIntSetting("heads", "stacksize");
        for (OfflinePlayer o : playerlist) {
            if(!this.playernames.contains(o.getUniqueId().toString())){
                this.playernames.add(o.getUniqueId().toString());
                ItemStack head = new ItemStack(Material.PLAYER_HEAD, stacksize);
                SkullMeta headmeta = (SkullMeta) head.getItemMeta();
                headmeta.setDisplayName(o.getName());
                headmeta.setOwningPlayer(o);
                head.setItemMeta(headmeta);
                this.PlayerHeadList.add(head);
            }
        }
    }

    private void loadHeadsFromConfig(){
        ArrayList<Playerhead> playerheads = this.plugin.getConfiguration().loadPlayerheadConfig();
        int stacksize = this.plugin.getConfiguration().getIntSetting("heads", "stacksize");
        for(Playerhead playerhead : playerheads){
            ItemStack head = new ItemStack(Material.PLAYER_HEAD, stacksize);
            playerhead.skull = SkullCreator.itemWithUrl(head, playerhead.texture, playerhead.name);
            this.PlayerHeadList.add(head);
        }
    }

    public void updateHeadList() {
        boolean extraheads = this.plugin.getConfiguration().getBooleanSetting("heads", "extraheads");
        this.updateHeadList(Bukkit.getOfflinePlayers());
        if (Bukkit.hasWhitelist()) {
            this.updateHeadList(Bukkit.getWhitelistedPlayers().toArray(
                new OfflinePlayer[Bukkit.getWhitelistedPlayers().size()]));    
        }
        this.updateHeadList(Bukkit.getOperators().toArray(
                new OfflinePlayer[Bukkit.getOperators().size()]));
        if(extraheads){
            this.loadHeadsFromConfig();
        }
        this.plugin.getLogger().info(PlayerHeadList.size() + " heads added to list.");
    }

    public ArrayList<ItemStack> getRandomHeads(){
        int max = this.plugin.getConfiguration().getIntSetting("heads", "max");
        int min = this.plugin.getConfiguration().getIntSetting("heads", "min");
        Random rand = new Random();
        int numheads = rand.nextInt(max-min+1)+min;
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        int headListSize = this.PlayerHeadList.size()-1;
        int[] triedindexes = new int[numheads];
        for(int k = 0; k<numheads&&k<headListSize; k++){
            int newIndex = rand.nextInt(headListSize+1);
            if(!Arrays.stream(triedindexes).anyMatch(i -> i == newIndex)){
                ret.add(this.PlayerHeadList.get(newIndex));
                triedindexes[k] = newIndex;
            }
        }
        return ret;
    }
}