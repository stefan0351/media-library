package com.kiwisoft.media.books;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;

import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.table.DefaultSortableTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.SortableTable;
import com.kiwisoft.swing.table.SortableTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * @author Stefan Stiller
 */
public class BookUpdateDialog extends JDialog
{
    public static final int CANCEL_OPTION=0;
    public static final int UPDATE_OPTION=1;
    public static final int NEW_OPTION=2;

    private int option;
    private Book book;
    private SortableTable table;
    private SortableTableModel<Book> tableModel;

    public BookUpdateDialog(Window owner, Collection<Book> books)
    {
        super(owner, "Choose Book to Update", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        initComponents(books);
        pack();
        GuiUtils.centerWindow(owner, this);
    }

    protected void initComponents(Collection<Book> books)
    {
        tableModel=new DefaultSortableTableModel<Book>("title", "author", "isbn", "publisher");
        for (Book book : books) tableModel.addRow(new BookTableRow(book));
        table=new SortableTable(tableModel);
        table.initializeColumns(new DefaultTableConfiguration(BookUpdateDialog.class, "books"));
        table.sizeColumnsToFit(true, true);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel.sort();
        table.getSelectionModel().setSelectionInterval(0, 0);
        JScrollPane tablePane=new JScrollPane(table);
        tablePane.setPreferredSize(new Dimension(700, 100));

        JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(new JButton(new UpdateAction()));
        pnlButtons.add(new JButton(new CreateNewAction()));
        pnlButtons.add(new JButton(new CancelAction()));

        JPanel panel=new JPanel(new GridBagLayout());
        panel.add(new JLabel("<html>This book already exists in the database.<br>Choose book to be updated or create a new one.</html>"),
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
        panel.add(tablePane,
                new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, CENTER, BOTH, new Insets(5, 10, 0, 10), 0, 0));
        panel.add(pnlButtons,
                new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        setContentPane(panel);
    }

    public int getOption()
    {
        return option;
    }

    public Book getBook()
    {
        return book;
    }

    private class UpdateAction extends AbstractAction
    {
        public UpdateAction()
        {
            super("Update Book");
        }

        public void actionPerformed(ActionEvent e)
        {
            int row=table.getSelectedRow();
            if (row>=0)
            {
                book=tableModel.getObject(row);
                if (book!=null)
                {
                    option=UPDATE_OPTION;
                    dispose();
                }
            }
        }
    }

    private class CreateNewAction extends AbstractAction
    {
        public CreateNewAction()
        {
            super("New Book");
        }

        public void actionPerformed(ActionEvent e)
        {
            option=NEW_OPTION;
            dispose();
        }
    }

    private class CancelAction extends AbstractAction
    {
        public CancelAction()
        {
            super("Cancel", Icons.getIcon("cancel"));
        }

        public void actionPerformed(ActionEvent e)
        {
            option=CANCEL_OPTION;
            dispose();
        }
    }
}
