package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.media.ContactMedium;
import com.kiwisoft.media.MediaManager;
import com.kiwisoft.media.dataimport.FanFicData;
import com.kiwisoft.media.dataimport.FanFictionNetLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.ResultTransactional;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.utils.FileUtils;
import org.htmlparser.util.ParserException;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Stefan Stiller
 * @since 01.01.2011
 */
public class FanFicImportUtils
{
	private FanFicImportUtils()
	{
	}

	public static void importFanFic(FanFicData ficData, FanFictionNetLoader loader) throws InvocationTargetException, InterruptedException, IOException, ParserException
	{
		final CreateTransactional transactional=new CreateTransactional(ficData, loader.getBaseUrl());
		transactional.fanDoms=getFanDoms(loader.getBaseUrl(), ficData);
		if (transactional.fanDoms==null) return;
		transactional.authors=getAuthors(loader.getBaseUrl(), ficData);
		if (transactional.authors==null) return;

		// Load chapters
		File tempDirectory=new File("tmp");
		tempDirectory.mkdirs();
		for (int i=1; i<=ficData.getChapterCount(); i++)
		{
			String chapter=loader.getChapter(i);
			File tempFile=File.createTempFile("chapter"+i, ".html", tempDirectory);
			tempFile.deleteOnExit();
			FileUtils.saveToFile(chapter, tempFile, "UTF-8");
			transactional.parts.add(new ChapterData(ficData.getChapters()!=null ? ficData.getChapters().get(i-1) : null, tempFile));
		}
		if (DBSession.execute(transactional))
		{
			ViewPanel currentView=MediaManager.getFrame().getCurrentView();
			final FanFicsSearchView fanficsView;
			if (currentView instanceof FanFicsSearchView) fanficsView=(FanFicsSearchView) currentView;
			else MediaManager.getFrame().setCurrentView(fanficsView=new FanFicsSearchView());
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					fanficsView.addFanFic(transactional.getResult());
				}
			});
		}

	}

	public static void updateFanFic(final FanFic fanFic, FanFicData data, FanFictionNetLoader loader) throws IOException, ParserException
	{
		List<FanFicPart> parts=fanFic.getParts().elements();
		for (int i=1; i<=data.getChapterCount(); i++)
		{
			final FanFicPart part=i<=parts.size() ? parts.get(i-1) : null;
			if (part!=null)
			{
				File contentFile=part.getContentFile();
				if (contentFile!=null) continue;
			}
			String partContent=loader.getChapter(i);
			boolean result=DBSession.execute(new UpdatePartTransactional(fanFic, part, data, i, partContent));
			if (!result) return;
		}
		if (data.isComplete())
		{
			DBSession.execute(new SetCompleteTransactional(fanFic));
		}

	}

	private static Set<Author> getAuthors(String baseUrl, FanFicData ficData) throws InvocationTargetException, InterruptedException
	{
		if (ficData.getAuthorUrl()==null)
		{
			infoMessage("No author URL found for URL "+baseUrl);
			return null;
		}
		Set<Author> authors=FanFicManager.getInstance().findAuthorsByUrl(ficData.getAuthorUrl());
		if (authors.size()>1)
		{
			infoMessage("Multiple authors found with URL "+ficData.getAuthorUrl());
			return null;
		}
		return authors;
	}

	private static Set<FanDom> getFanDoms(String baseUrl, FanFicData ficData) throws InvocationTargetException, InterruptedException
	{
		if (ficData.getDomainUrl()==null)
		{
			infoMessage("No domain URL found for URL "+baseUrl);
			return null;
		}
		Set<FanDom> fanDoms=FanFicManager.getInstance().findFanDomsByUrl(ficData.getDomainUrl());
		if (fanDoms.isEmpty())
		{
			infoMessage("No fanfic domain found with URL "+ficData.getDomainUrl());
			return null;
		}
		if (fanDoms.size()>1)
		{
			infoMessage("Multiple fanfic domains found with URL "+ficData.getDomainUrl());
			return null;
		}
		return fanDoms;
	}

	private static void infoMessage(final String message) throws InterruptedException, InvocationTargetException
	{
		SwingUtilities.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}


	private static class UpdatePartTransactional implements Transactional
	{
		private FanFic fanFic;
		private FanFicPart part;
		private FanFicData data;
		private int chapter;
		private String content;

		private UpdatePartTransactional(FanFic fanFic, FanFicPart part, FanFicData data, int chapter, String content)
		{
			this.fanFic=fanFic;
			this.part=part;
			this.data=data;
			this.chapter=chapter;
			this.content=content;
		}

		@Override
		public void run() throws Exception
		{
			if (part==null) part=fanFic.createPart();
			if (data.getChapterCount()>1) part.setName(data.getChapters().get(chapter-1));
			part.setType(FanFicPart.TYPE_HTML);
			part.putContent(new ByteArrayInputStream(content.getBytes("UTF-8")), "html", "UTF-8");
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			GuiUtils.handleThrowable(null, throwable);
		}
	}

	private static class SetCompleteTransactional implements Transactional
	{
		private final FanFic fanFic;

		public SetCompleteTransactional(FanFic fanFic)
		{
			this.fanFic=fanFic;
		}

		@Override
		public void run() throws Exception
		{
			fanFic.setFinished(true);
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			GuiUtils.handleThrowable(null, throwable);
		}
	}

	private static class ChapterData
	{
		private String title;
		private File file;

		private ChapterData(String title, File file)
		{
			this.title=title;
			this.file=file;
		}
	}

	private static class CreateTransactional extends ResultTransactional<FanFic>
	{
		private String baseUrl;
		private List<ChapterData> parts;
		private FanFicData ficData;
		private Set<FanDom> fanDoms;
		private Set<Author> authors;

		public CreateTransactional(FanFicData ficData, String baseUrl)
		{
			this.ficData=ficData;
			this.baseUrl=baseUrl;
			parts=new ArrayList<ChapterData>(ficData.getChapterCount());
		}

		@Override
		public void run() throws Exception
		{
			result=FanFicManager.getInstance().createFanFic();
			result.setTitle(ficData.getTitle());
			result.setFinished(ficData.isComplete());
			result.setUrl(baseUrl);
			result.setDescription(ficData.getSummary());
			result.setFanDoms(fanDoms);
			result.setRating(ficData.getRating());
			if (authors.isEmpty())
			{
				Author author=FanFicManager.getInstance().createAuthor();
				author.setName(ficData.getAuthor());
				ContactMedium web=author.createWeb();
				web.setValue(ficData.getAuthorUrl());
				authors=Collections.singleton(author);
			}
			result.setAuthors(authors);
			for (ChapterData part : parts)
			{
				FanFicPart ficPart=result.createPart();
				ficPart.setName(part.title);
				ficPart.setType(FanFicPart.TYPE_HTML);
				ficPart.putContent(new FileInputStream(part.file), "html", "UTF-8");
			}
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			GuiUtils.handleThrowable(null, throwable);
		}
	}
}
