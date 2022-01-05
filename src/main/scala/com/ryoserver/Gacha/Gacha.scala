package com.ryoserver.Gacha

import com.ryoserver.Gacha.GachaCoolDown.{getCoolDown, pullCoolDownSet}
import com.ryoserver.Player.PlayerManager.setPlayerData
import com.ryoserver.Player.RyoServerPlayer
import com.ryoserver.RyoServerAssist
import com.ryoserver.Title.GiveTitle
import org.bukkit.ChatColor._
import org.bukkit.Sound
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, EventPriority, Listener}

class Gacha(ryoServerAssist: RyoServerAssist) extends Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  def onPull(e: PlayerInteractEvent): Unit = {
    val p = e.getPlayer
    val inventory = p.getInventory
    val mainHand = inventory.getItemInMainHand
    val getItemAmount = inventory.getItemInMainHand.getAmount
    if (e.getAction == Action.RIGHT_CLICK_AIR || e.getAction == Action.RIGHT_CLICK_BLOCK) {
      if (((mainHand.getType == GachaPaperData.normal.getType && mainHand.getItemMeta == GachaPaperData.normal.getItemMeta) ||
        (mainHand.getType == GachaPaperData.fromAdmin.getType && mainHand.getItemMeta == GachaPaperData.fromAdmin.getItemMeta))
        && p.isSneaking && !getCoolDown(p)) {
        /*
        シフトと右クリックを同時に行った場合(全部引く場合)
      */
        var special = 0
        var per = 0
        var bigPer = 0
        var miss = 0
        inventory.getItemInMainHand.setAmount(0)
        for (_ <- 0 until getItemAmount) {
          new GachaLottery().lottery() match {
            case rarity.special =>
              special += 1
              p.getWorld.dropItem(p.getLocation(), new GachaLottery().itemLottery(4))
            case rarity.per =>
              per += 1
              p.getWorld.dropItem(p.getLocation(), new GachaLottery().itemLottery(2))
            case rarity.bigPer =>
              bigPer += 1
              p.getWorld.dropItem(p.getLocation(), new GachaLottery().itemLottery(3))
            case rarity.miss =>
              miss += 1
              p.getWorld.dropItem(p.getLocation(), new GachaLottery().itemLottery(1))
          }
        }
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1)
        p.sendMessage(s"$AQUA${getItemAmount}回ガチャを引きました！")
        p.sendMessage(s"${AQUA}特等x$special,大当たりx$bigPer,あたりx$per,はずれx${miss}個出ました！")
        pullCoolDownSet(p, ryoServerAssist)
        p.addGachaPullNumber(getItemAmount)
        new GiveTitle(ryoServerAssist).gachaPullNumber(p)
      } else if (((mainHand.getType == GachaPaperData.normal.getType && mainHand.getItemMeta == GachaPaperData.normal.getItemMeta) || (mainHand.getType == GachaPaperData.fromAdmin.getType && mainHand.getItemMeta == GachaPaperData.fromAdmin.getItemMeta))
        && !getCoolDown(p)) {
        /*
        1回だけ引く場合
      */
        inventory.getItemInMainHand.setAmount(getItemAmount - 1)
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1)
        new GachaLottery().lottery() match {
          case rarity.special =>
            p.sendMessage(s"${AQUA}特等！")
            p.getWorld.dropItem(p.getLocation(), new GachaLottery().itemLottery(4))
          case rarity.per =>
            p.sendMessage(s"${AQUA}あたり！")
            p.getWorld.dropItem(p.getLocation(), new GachaLottery().itemLottery(2))
          case rarity.bigPer =>
            p.sendMessage(s"${AQUA}大当たり！")
            p.getWorld.dropItem(p.getLocation(), new GachaLottery().itemLottery(3))
          case rarity.miss =>
            p.sendMessage(s"${AQUA}はずれ！")
            p.getWorld.dropItem(p.getLocation(), new GachaLottery().itemLottery(1))
        }
        pullCoolDownSet(p, ryoServerAssist)
        p.addGachaPullNumber(1)
        new GiveTitle(ryoServerAssist).gachaPullNumber(p)
      }
    }
  }


}
