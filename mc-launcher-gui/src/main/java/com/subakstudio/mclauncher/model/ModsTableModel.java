package com.subakstudio.mclauncher.model;

import com.subakstudio.mclauncher.MinecraftDataFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Thomas on 1/3/2016.
 */
public class ModsTableModel extends AbstractTableModel {
    private final Logger log;
    private List<TableModelListener> listeners = new ArrayList<TableModelListener>();
    private List<ModsTableRow> mods = new ArrayList<ModsTableRow>();
    private List<ModsTableRow> selected = new ArrayList<ModsTableRow>();

    public ModsTableModel() {
        log = LoggerFactory.getLogger(ModsTableModel.class);
    }

    public void setMcDataFolder(String path) {
        mods.clear();
        selected.clear();

        File dataFolder = new File(path);

        if (dataFolder.exists()) {
            if (MinecraftDataFolder.getModsFolder(dataFolder).exists()) {
                File[] modFiles = MinecraftDataFolder.getModsFolder(dataFolder).listFiles();
                for (File file : modFiles) {
                    ModsTableRow row = new ModsTableRow();
                    row.file = file;
                    row.checked = false;
                    mods.add(row);
                    selected.add(row);
                }
                log.info("mods files are loaded under mods folder.");
            } else {
                log.warn("No mods folder.");
            }
            if (MinecraftDataFolder.getDisabledModsFolder(dataFolder).exists()) {
                File[] modFiles = MinecraftDataFolder.getDisabledModsFolder(dataFolder).listFiles();
                for (File file : modFiles) {
                    ModsTableRow row = new ModsTableRow();
                    row.file = file;
                    row.checked = false;
                    mods.add(row);
                }
                log.info("mods files are loaded under disabled mods folder.");
            } else {
                log.warn("No disabled mods folder.");
            }
        }

        mods.sort(new Comparator<ModsTableRow>() {
            @Override
            public int compare(ModsTableRow o1, ModsTableRow o2) {
                String str1 = o1.file.getName();
                String str2 = o2.file.getName();
                int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
                if (res == 0) {
                    res = str1.compareTo(str2);
                }
                return res;
            }
        });

        for (TableModelListener l : listeners) {
            l.tableChanged(new TableModelEvent(this));
        }
    }

    public ModsTableRow getRow(int row) {
        return mods.get(row);
    }

    @Override
    public int getRowCount() {
        return mods.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ResourceBundle.getBundle("strings").getString("col.name");
            case 1:
                return ResourceBundle.getBundle("strings").getString("col.install");
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 ? true : false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ModsTableRow row = mods.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.file.getName();
            case 1:
                return selected.contains(row);
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            log.debug("setValueAt: " + aValue);
            boolean bValue = Boolean.valueOf((Boolean) aValue);
            if (bValue) {
                selected.add(mods.get(rowIndex));
            } else {
                selected.remove(mods.get(rowIndex));
            }
            for (TableModelListener l : listeners) {
                l.tableChanged(new TableModelEvent(this, rowIndex));
            }
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1) {
            return Boolean.class;
        }
        return super.getColumnClass(columnIndex);
    }

    public java.util.List<ModsTableRow> getSelected() {
        return selected;
    }
}