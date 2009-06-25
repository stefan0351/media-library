package com.kiwisoft.media.books;

import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.utils.StringUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Stefan Stiller
 */
public class BookTableRow extends SortableTableRow<Book> implements PropertyChangeListener
{
    public BookTableRow(Book book)
    {
        super(book);
    }

    @Override
	public void installListener()
    {
        getUserObject().addPropertyChangeListener(this);
    }

    @Override
	public void removeListener()
    {
        getUserObject().removePropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        fireRowUpdated();
    }

    @Override
	public Object getDisplayValue(int column, String property)
    {
        if ("title".equals(property)) return getUserObject().getTitle();
        if ("author".equals(property)) return StringUtils.formatAsEnumeration(getUserObject().getAuthors(), "; ");
        else if ("publisher".equals(property)) return getUserObject().getPublisher();
        else if ("pageCount".equals(property))
        {
            Integer pageCount=getUserObject().getPageCount();
            if (pageCount!=null && pageCount>0) return pageCount;
            return null;
        }
        else if ("binding".equals(property)) return getUserObject().getBinding();
        else if ("isbn".equals(property))
        {
            String isbn=getUserObject().getIsbn13();
            if (StringUtils.isEmpty(isbn)) isbn=getUserObject().getIsbn10();
            return isbn;
        }
        return null;
    }
}
