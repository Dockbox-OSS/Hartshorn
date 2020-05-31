package com.darwinreforged.server.modules.extensions.plotsquared.oldplot;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "plot")
public class PlotStorageModel {

    @DatabaseField(dataType = DataType.INTEGER, canBeNull = false, columnName = "id")
    private int id;

    @DatabaseField(dataType = DataType.INTEGER, canBeNull = false, columnName = "plot_id_x")
    private int plot_id_x;

    @DatabaseField(dataType = DataType.INTEGER, canBeNull = false, columnName = "plot_id_z")
    private int plot_id_z;

    @DatabaseField(dataType = DataType.UUID, canBeNull = false, columnName = "owner")
    private UUID owner;

    @DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = "world")
    private String world;

    @DatabaseField(dataType = DataType.DATE, canBeNull = false, columnName = "timestamp")
    private Date timestamp;

    public int getId() {
        return id;
    }

    public int getPlot_id_x() {
        return plot_id_x;
    }

    public int getPlot_id_z() {
        return plot_id_z;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getWorld() {
        return world;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
