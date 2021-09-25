package com.ryoserver.util

import com.ryoserver.RyoServerAssist

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Statement}

class SQL(ryoServerAssist: RyoServerAssist) {

  private val driver = "com.mysql.cj.jdbc.Driver"
  private val URL = s"jdbc:mysql://${ryoServerAssist.getConfig.getString("host")}/${ryoServerAssist.getConfig.getString("db")}?autoReconnect=true&useSSL=false"
  private val USER = ryoServerAssist.getConfig.getString("user")
  private val PASS = ryoServerAssist.getConfig.getString("pw")
  private var con: Connection = _
  private var stmt:Statement = _
  private var rs:ResultSet = _
  private var ps:PreparedStatement = _

  def connectionTest(): Boolean = {
    try {
      Class.forName(this.driver)
      this.con = DriverManager.getConnection(this.URL, this.USER, this.PASS)
      this.con.close()
      true
    } catch {
      case _:Exception => false
    }
  }

  def executeQuery(query:String): ResultSet = {
    Class.forName(this.driver)
    this.con = DriverManager.getConnection(this.URL,this.USER,this.PASS)
    this.stmt = this.con.createStatement()
    stmt.setQueryTimeout(5)
    this.rs = this.stmt.executeQuery(query)
    this.rs
  }

  def executeSQL(sql:String): Unit = {
    Class.forName(this.driver)
    this.con = DriverManager.getConnection(this.URL,this.USER,this.PASS)
    this.ps = this.con.prepareStatement(sql)
    this.ps.executeUpdate()
    this.ps.close()
    this.con.close()
  }

  def purseFolder(sql:String,quote:String): Unit = {
    Class.forName(this.driver)
    this.con = DriverManager.getConnection(this.URL,this.USER,this.PASS)
    this.ps = this.con.prepareStatement(sql)
    ps.setString(1,quote)
    this.ps.executeUpdate()
    this.ps.close()
    this.con.close()
  }

  def close(): Unit = {
    if (this.con != null)this.con.close()
    if (this.stmt != null)this.stmt.close()
    if (this.ps != null)this.ps.close()
    if (this.rs != null)this.rs.close()
  }

}