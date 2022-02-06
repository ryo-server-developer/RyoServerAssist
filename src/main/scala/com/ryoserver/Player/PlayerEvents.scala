package com.ryoserver.Player

import com.ryoserver.Maintenance.MaintenanceData.getMaintenance
import com.ryoserver.RyoServerAssist
import com.ryoserver.Title.GiveTitle
import org.bukkit.event.player.{PlayerJoinEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, Listener}
import scalikejdbc.{AutoSession, scalikejdbcSQLInterpolationImplicitDef}

class PlayerEvents(implicit ryoServerAssist: RyoServerAssist) extends Listener {

  @EventHandler
  def onJoin(e: PlayerJoinEvent): Unit = {
    val p = e.getPlayer
    if (getMaintenance && !p.hasPermission("ryoserverassist.maintenance")) {
      p.kickPlayer("現在メンテナンス中です。\n\n詳細は公式Twitter、Discordを御覧ください。")
    }
    new PlayerDataLoader().load(p)
    val title = new GiveTitle()
    title.continuousLogin(p)
    title.loginDays(p)
    title.loginYear(p)
    title.loginPeriod(p)
    title.loginDay(p)
    title.continuousLoginAndQuestClearNumber(p)
  }

  @EventHandler
  def onQuit(e: PlayerQuitEvent): Unit = {
    new PlayerDataLoader().unload(e.getPlayer)
    implicit val session: AutoSession.type = AutoSession
    sql"UPDATE Players SET lastLogout=NOW() WHERE UUID=${e.getPlayer.getUniqueId.toString}".execute.apply()
  }

}
