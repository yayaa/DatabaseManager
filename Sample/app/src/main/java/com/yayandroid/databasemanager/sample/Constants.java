package com.yayandroid.databasemanager.sample;

/**
 * Created by Yahya Bayramoglu on 30/12/15.
 */
public class Constants {

    public static final String DB_TAG_LOCAL = "localDB";
    public static final String DB_TAG_ASSETS = "assetsDB";
    public static final String DB_TAG_DISC = "discDB";

    public static final String TABLE_TICKET = "tbl_Ticket";

    public static final String INTENT_KEY_TAG = "databaseTag";
    public static final String INTENT_KEY_LIST = "ticketList";

    public static final String SELECT_TICKETS = "SELECT * FROM " + Constants.TABLE_TICKET;
    public static final String INSERT_TICKET = "INSERT INTO " + Constants.TABLE_TICKET + " VALUES (?, ?)";
    public static final String DELETE_TICKET = "DELETE FROM " + Constants.TABLE_TICKET + " WHERE ticketId=?";

    public static final String COMPARISON_QUERY = "SELECT discTickets.TicketId, discTickets.CreateDate"
            + " FROM " + Constants.TABLE_TICKET + " AS discTickets"
            + " LEFT JOIN " + Constants.DB_TAG_ASSETS + "." + Constants.TABLE_TICKET + " AS assetsTickets"
            + " ON discTickets.TicketId = assetsTickets.TicketId"
            + " WHERE assetsTickets.TicketId IS NULL";

}