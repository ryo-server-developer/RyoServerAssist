package com.ryoserver.Menu

import com.ryoserver.Distribution.Distribution
import com.ryoserver.DustBox.DustBoxInventory
import com.ryoserver.Gacha.getGachaTickets
import com.ryoserver.Quest.QuestSelectInventory
import com.ryoserver.RyoServerAssist
import com.ryoserver.SimpleRegion.RegionMenu
import com.ryoserver.SkillSystems.Skill.SelectSkillMenu
import com.ryoserver.Storage.Storage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

class MenuEvent(ryoServerAssist: RyoServerAssist) extends Listener {

  @EventHandler
  def onClick(e: InventoryClickEvent): Unit = {
    if (!e.getView.getTitle.equalsIgnoreCase("りょう鯖メニュー")) return
    e.setCancelled(true)
    val p = e.getWhoClicked.asInstanceOf[Player]
    e.getSlot match {
      case 0 => new Distribution(ryoServerAssist).receipt(p)
      case 2 => new Storage(ryoServerAssist).load(p)
      case 4 => new QuestSelectInventory(ryoServerAssist).selectInventory(p)
      case 6 => new getGachaTickets(ryoServerAssist).receipt(p)
      case 8 => new SelectSkillMenu(ryoServerAssist).openMenu(p)
      case 9 => new RegionMenu().menu(p)
      case 11 => new DustBoxInventory().openDustBox(p)
      case _ =>
    }
  }

  @EventHandler
  def stickClick(e: PlayerInteractEvent): Unit = {
    if ((e.getAction == Action.RIGHT_CLICK_BLOCK || e.getAction == Action.RIGHT_CLICK_AIR) &&
      e.getPlayer.getInventory.getItemInMainHand.getType == Material.STICK) {
      e.getPlayer.openInventory(createMenu.menu(e.getPlayer,ryoServerAssist))
    }
  }

}
