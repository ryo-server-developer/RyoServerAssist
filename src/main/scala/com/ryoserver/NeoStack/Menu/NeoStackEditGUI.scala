package com.ryoserver.NeoStack.Menu

import com.ryoserver.Menu.{Menu, createMenu}
import com.ryoserver.NeoStack.PlayerCategory.getSelectedCategory
import com.ryoserver.NeoStack.{ItemList, LoadNeoStackPage, NeoStackData}
import com.ryoserver.RyoServerAssist
import com.ryoserver.util.SQL
import org.bukkit.ChatColor._
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.{ChatColor, Material}

import scala.collection.mutable

class NeoStackEditGUI(ryoServerAssist: RyoServerAssist) extends Menu {
  override var name: String = _
  override val slot: Int = 6
  override var p: Player = _

  def openAddGUI(player:Player,page:Int,category:String): Unit = {
    p = player
    name = "neoStackアイテム追加メニュー:" + page
    val sql = new SQL(ryoServerAssist)
    val rs = sql.executeQuery(s"SELECT * FROM StackList WHERE page=$page AND category='$category';")
    var invContents = ""
    if (rs.next()) invContents = rs.getString("invItem")
    var index = 0
    invContents.split(';').foreach(invContent => {
      val config = new YamlConfiguration
      config.loadFromString(invContent)
      if (invContent != null) {
        setItemStack(index % 9 + 1,(index / 9) + 1, config.getItemStack("i", null))
      }
      index += 1
    })
    setItem(1,6,Material.MAGENTA_GLAZED_TERRACOTTA,effect = false,s"${GREEN}前のページに戻ります。",List(s"${GRAY}クリックで戻ります。"))
    if (p.hasPermission("ryoserverassist.neoStack")) {
      setItem(5,6,Material.NETHER_STAR,effect = false,"クリックでリストを保存します。",List("カテゴリ:" + getSelectedCategory(p) + "のリストを保存します。"))
    }
    setItem(9,6,Material.MAGENTA_GLAZED_TERRACOTTA,effect = false,s"${GREEN}次のページに移動します。",List(s"${GRAY}クリックで移動します。"))
    partButton = true
    buttons :+= 45
    buttons :+= 49
    buttons :+= 53
    registerMotion(motion)
    open()
  }

  def motion(p: Player,index:Int): Unit = {
    val data = new NeoStackData(ryoServerAssist)
    val nowPage = p.getOpenInventory.getTitle.replace("neoStackアイテム追加メニュー:","").toInt
    if (index == 49) {
      var invIndex = 0
      var invItem = ""
      p.getOpenInventory.getTopInventory.getContents.foreach(is => {
        if (invIndex < 45) {
          val config = new YamlConfiguration
          config.set("i",is)
          invItem += config.saveToString() + ";"
        }
        invIndex += 1
      })
      data.editItemList(getSelectedCategory(p),nowPage,invItem)
      p.sendMessage(ChatColor.AQUA  + "カテゴリリスト:" + getSelectedCategory(p) + "を編集しました。")
      new LoadNeoStackPage(ryoServerAssist).loadStackPage()
      ItemList.stackList = mutable.Map.empty
      ItemList.loadItemList(ryoServerAssist)
    } else if (index == 45) {
      if (nowPage != 1) {
        new NeoStackEditGUI(ryoServerAssist).openAddGUI(p,nowPage - 1,getSelectedCategory(p))
      } else if (nowPage == 1) {
        new createMenu(ryoServerAssist).menu(p,ryoServerAssist)
      }
    } else if (index == 53) {
      new NeoStackEditGUI(ryoServerAssist).openAddGUI(p,nowPage + 1,getSelectedCategory(p))
    }
  }

}
