package com.kiwisoft.media;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.files.*;
import com.kiwisoft.media.links.OpenLinkFieldAction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.ActionField;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.SortableTable;
import com.kiwisoft.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChannelDetailsView extends DetailsView
{
    public static void create(Channel channel)
    {
        new DetailsFrame(new ChannelDetailsView(channel)).show();
    }

    private Channel channel;

    // Konfigurations Panel
    private JTextField nameField;
    private LookupField<MediaFile> logoField;
    private LookupField<Language> languageField;
    private JCheckBox receivingField;
    private NamesTableModel namesTableModel;
    private ActionField webAddressField;

    private ChannelDetailsView(Channel channel)
    {
        createContentPanel();
        setChannel(channel);
    }

    protected void createContentPanel()
    {
        nameField=new JTextField();
        languageField=new LookupField<Language>(new LanguageLookup());
        webAddressField=new ActionField(new OpenLinkFieldAction());
        receivingField=new JCheckBox();
        logoField=new LookupField<MediaFile>(new MediaFileLookup(MediaType.IMAGE), new ImageLookupHandler()
        {
            @Override
            public String getDefaultName()
            {
                return nameField.getText()+" - Logo";
            }
        });
        ImagePanel logoPreview=new ImagePanel(new Dimension(50, 30));
        logoPreview.setBorder(new EtchedBorder());
        namesTableModel=new NamesTableModel(false);
        SortableTable tblNames=new SortableTable(namesTableModel);
        tblNames.configure(new DefaultTableConfiguration("channel.names", ChannelDetailsView.class, "names"));

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(400, 250));
        int row=0;
        add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
                                                        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(nameField, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
                                              GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

        row++;
        add(new JLabel("Language:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
                                                            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        add(languageField, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
                                                  GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
        row++;
        add(new JLabel("Web Address:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
                                                               GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        add(webAddressField, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
                                                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

        row++;
        add(new JLabel("Available:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
                                                             GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        add(receivingField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
                                                   GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

        row++;
        add(new JLabel("Logo:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
                                                        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        add(logoField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
                                              GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
        add(logoPreview, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

        row++;
        add(new JLabel("Other Names:"), new GridBagConstraints(0, row, 3, 1, 0.0, 0.0,
                                                               GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));

        row++;
        add(new JScrollPane(tblNames), new GridBagConstraints(0, row, 3, 1, 1.0, 1.0,
                                                              GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));

        nameField.getDocument().addDocumentListener(new FrameTitleUpdater());
        new PicturePreviewUpdater(logoField, logoPreview);
    }

    private void setChannel(Channel channel)
    {
        this.channel=channel;
        if (channel!=null)
        {
            nameField.setText(channel.getName());
            languageField.setValue(channel.getLanguage());
            logoField.setValue(channel.getLogo());
            receivingField.setSelected(channel.isReceivable());
            webAddressField.setText(channel.getWebAddress());
            Iterator it=channel.getAltNames().iterator();
            while (it.hasNext())
            {
                Name name=(Name) it.next();
                namesTableModel.addName(name.getName(), name.getLanguage());
            }
            namesTableModel.sort();
        }
        else
        {
            languageField.setValue(LanguageManager.getInstance().getLanguageBySymbol("de"));
            receivingField.setSelected(true);
        }
    }

    @Override
	public boolean apply()
    {
        String name=nameField.getText();
        if (StringUtils.isEmpty(name))
        {
            JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        Language language=languageField.getValue();
        if (language==null)
        {
            JOptionPane.showMessageDialog(this, "Language is missing!", "Error", JOptionPane.ERROR_MESSAGE);
            languageField.requestFocus();
            return false;
        }
        boolean receiving=receivingField.isSelected();
        MediaFile logo=logoField.getValue();
        String webAddress=webAddressField.getText();
        Set<String> names=namesTableModel.getNameSet();

        Transaction transaction=null;
        try
        {
            transaction=DBSession.getInstance().createTransaction();
            if (channel==null) channel=ChannelManager.getInstance().createChannel();
            channel.setName(name);
            channel.setLogo(logo);
            channel.setLanguage(language);
            channel.setReceivable(receiving);
            channel.setWebAddress(webAddress);
            for (Name aName : new HashSet<Name>(channel.getAltNames()))
            {
                if (names.contains(aName.getName())) names.remove(aName.getName());
                else channel.dropAltName(aName);
            }
            for (String aName : names)
            {
                Name altName=channel.createAltName();
                altName.setName(aName);
            }
            transaction.close();
            return true;
        }
        catch (Exception e)
        {
            if (transaction!=null)
            {
                try
                {
                    transaction.rollback();
                }
                catch (SQLException e1)
                {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private class FrameTitleUpdater extends DocumentAdapter
    {
        @Override
		public void changedUpdate(DocumentEvent e)
        {
            String name=nameField.getText();
            if (StringUtils.isEmpty(name)) name="<unknown>";
            setTitle("Channel: "+name);
        }
    }
}
