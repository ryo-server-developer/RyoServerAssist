package com.ryoserver.Quest

import com.ryoserver.Inventory.Item.getItem
import com.ryoserver.Level.Player.getPlayerData
import com.ryoserver.RyoServerAssist
import org.bukkit.entity.{EntityType, Player}
import org.bukkit.inventory.{Inventory, ItemStack}
import org.bukkit.{Bukkit, ChatColor, Material}

import java.util
import scala.jdk.CollectionConverters._

class QuestSelectInventory(ryoServerAssist: RyoServerAssist) {

  def selectInventory(p:Player): Unit = {
    val questData = new QuestData(ryoServerAssist)
    if (questData.getSelectedQuest(p) == null) {
      var selectedQuests: Array[String] = Array.empty[String]
      val playerLevel = new getPlayerData(ryoServerAssist).getPlayerLevel(p)
      val inv = Bukkit.createInventory(null, 27, "クエスト選択")
      for (i <- 1 to 7 by 2) {
        val data = questData.loadQuest(p)
        val lottery = new LotteryQuest()
        if (data.isEmpty) {
          var loop = true
          do {
            lottery.lottery(playerLevel)
            if (!selectedQuests.contains(lottery.questName)) {
              loop = false
              selectedQuests :+= lottery.questName
            }
          } while (loop)
        } else {
          lottery.questName = data((i - 1) / 2)
          lottery.loadQuestData()
          selectedQuests :+= lottery.questName
        }
        val questType = if (lottery.questType.equalsIgnoreCase("delivery")) "納品クエスト"
        else if (lottery.questType.equalsIgnoreCase("suppression")) "討伐クエスト"
        val questDetails: java.util.List[String] = new util.ArrayList[String]()
        questDetails.add(ChatColor.WHITE + "【リスト】")
        if (questType == "納品クエスト") {
          lottery.items.forEach(i => {
            val material = Material.matchMaterial(i.split(":")(0))
            val itemStack = new ItemStack(material)
            var itemName = ""
            if (material.isBlock) itemName = "block." + itemStack.getType.getKey.toString.replace(":", ".")
            else if (material.isItem) itemName = "item." + itemStack.getType.getKey.toString.replace(":", ".")
            questDetails.add(ChatColor.WHITE + "・" + loadQuests.langFile.get(itemName).textValue() + ":" + i.split(":")(1) + "個")
          })
        } else if (questType == "討伐クエスト") {
          lottery.mobs.forEach(i => {
            val entity = getEntity(i.split(":")(0))
            questDetails.add(ChatColor.WHITE + "・" + loadQuests.langFile.get("entity." + entity.getKey.toString.replace(":",".")).textValue() +
            ":" + i.split(":")(1) + "体")
          })
        }
        questDetails.add(ChatColor.WHITE + "【説明】")
        lottery.descriptions.forEach(description => questDetails.add(ChatColor.WHITE + "・" + description))
        questDetails.add(ChatColor.WHITE + "このクエストを完了した際に得られる経験値量:" + lottery.exp)
        inv.setItem(i, getItem(Material.BOOK, s"[$questType]" + lottery.questName, questDetails))
      }
      inv.setItem(22, getItem(Material.NETHER_STAR, "クエスト更新", List("クリックでクエストを更新します。").asJava))
      inv.setItem(26, getItem(Material.MAGENTA_GLAZED_TERRACOTTA, "メニューに戻る", List("クリックでメニューに戻ります。").asJava))
      p.openInventory(inv)
      new QuestData(ryoServerAssist).saveQuest(p, selectedQuests)
    } else {
      var deliveryInv:Inventory = null
      val lottery = new LotteryQuest()
      lottery.questName = questData.getSelectedQuest(p)
      lottery.loadQuestData()
      val questType = if (lottery.questType.equalsIgnoreCase("delivery")) "納品クエスト"
      else if (lottery.questType.equalsIgnoreCase("suppression")) "討伐クエスト"
      val questDetails: java.util.List[String] = new util.ArrayList[String]()
      questDetails.add(ChatColor.WHITE + "【残りリスト】")
      if (questType == "納品クエスト") {
        deliveryInv = Bukkit.createInventory(null,54,"納品")
        questData.getSelectedQuestRemaining(p).split(";").foreach(i => {
          val material = Material.matchMaterial(i.split(":")(0))
          val itemStack = new ItemStack(material)
          var itemName = ""
          if (material.isBlock) itemName = "block." + itemStack.getType.getKey.toString.replace(":", ".")
          else if (material.isItem) itemName = "item." + itemStack.getType.getKey.toString.replace(":", ".")
          questDetails.add(ChatColor.WHITE + "・" + loadQuests.langFile.get(itemName).textValue() + ":" + i.split(":")(1) + "個")
        })
      } else if (questType == "討伐クエスト") {
        deliveryInv = Bukkit.createInventory(null,54,"討伐")
        questData.getSelectedQuestRemaining(p).split(";").foreach(e => {
          val entity = getEntity(e.split(":")(0))
          questDetails.add(ChatColor.WHITE + "・" + loadQuests.langFile.get("entity." + entity.getKey.toString.replace(":",".")).textValue()
            + ":" + e.split(":")(1) + "体")
        })
      }
      questDetails.add(ChatColor.WHITE + "【説明】")
      lottery.descriptions.forEach(description => questDetails.add(ChatColor.WHITE + "・" + description))
      questDetails.add(ChatColor.WHITE + "このクエストを完了した際に得られる経験値量:" + lottery.exp)
      deliveryInv.setItem(45, getItem(Material.BOOK, s"[$questType]" + lottery.questName, questDetails))
      if (questType == "納品クエスト") deliveryInv.setItem(46,getItem(Material.NETHER_STAR,"納品する",List("クリックで納品します。").asJava))
      deliveryInv.setItem(53,getItem(Material.RED_WOOL,"クエストを中止する",List("クリックでクエストを中止します。","納品したアイテムは戻りません！").asJava))
      p.openInventory(deliveryInv)
    }
  }

  def getEntity(name:String): EntityType = {
    EntityType.values().foreach(entity => if (entity.name().equalsIgnoreCase(name)) return entity)
    null
  }
}
