package com.djekgrif.alternativeradio.manager;

import android.text.Html;
import android.text.TextUtils;

import com.djekgrif.alternativeradio.common.Logger;
import com.djekgrif.alternativeradio.common.events.SongTextItemEvent;
import com.djekgrif.alternativeradio.network.SimpleSubscriber;
import com.djekgrif.alternativeradio.network.model.CurrentTrackInfo;
import com.djekgrif.alternativeradio.network.model.SearchInfoDetails;
import com.djekgrif.alternativeradio.network.model.SongTextInfoItem;
import com.djekgrif.alternativeradio.network.model.SongTextItem;
import com.djekgrif.alternativeradio.ui.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.EOFException;
import java.net.URLEncoder;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

/**
 * Created by djek-grif on 7/4/17.
 */

public class SongTextHelper {

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/600.7.12 (KHTML, like Gecko) Version/8.0.7 Safari/600.7.12";
    private static final String SEARCH_TEXT_LINK = "http://search.azlyrics.com/search.php?q=";
    private static Subscription searchTextSubscription;

    public static void searchTextOfSong(final CurrentTrackInfo currentTrackInfo) {
        if (searchTextSubscription != null) {
            if (!searchTextSubscription.isUnsubscribed()) {
                searchTextSubscription.unsubscribe();
            }
        }
        String trackName = StringUtils.cleanSongInfoString(currentTrackInfo.getTrackName());
        String artistName = StringUtils.cleanSongInfoString(currentTrackInfo.getArtistName());
        searchTextSubscription = Observable.just(SEARCH_TEXT_LINK + URLEncoder.encode(TextUtils.join(" ", new String[]{artistName, trackName})))
                .map(link -> {
                    try {
                        return Jsoup.connect(link).header("User-agent", USER_AGENT).get();
                    } catch (Exception e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .filter(document -> document != null)
                .map(document -> {
                    Elements panelElements = document.body().getElementsByClass("main-page").get(0).getElementsByClass("row").get(0).getAllElements().get(0).getElementsByClass("panel");
                    if (panelElements != null && !panelElements.isEmpty()) {
                        Element panel = panelElements.get(0);
                        String searchResultText = panel.getElementsByClass("panel-heading").get(0).text();
                        if (!TextUtils.isEmpty(searchResultText)) {
                            int startIndex = searchResultText.indexOf("of");
                            int endIndex = searchResultText.indexOf("total");
                            if (startIndex != -1 && endIndex != -1) {
                                String resultCount = searchResultText.substring(startIndex + 2, endIndex);
                                int count = Integer.parseInt(resultCount.trim());
                                if (count > 0) {
                                    return panel.getElementsByClass("visitedlyr");
                                }
                            }
                        }
                    } else {
                        Logger.e("Error get text page", Logger.SONG_INFO);
                        throw Exceptions.propagate(new IllegalArgumentException("Error get link for text page"));
                    }
                    return null;
                })
                .filter(elementsHtml -> elementsHtml != null && !elementsHtml.isEmpty())
                .flatMap(elementsHtml -> Observable.from(elementsHtml)
                        .map(element -> {
                            String link = element.select("a").attr("href");
                            String title = element.select("a").text();
                            return new SongTextInfoItem(title, link, "");
                        }))
                .toList()
                .filter(list -> list != null && !list.isEmpty())
                .take(1)
                .map(songTextInfoItems -> songTextInfoItems.get(0))
                .flatMap(songTextInfoItem -> Observable.fromCallable(() -> {
                            try {
                                return Jsoup.connect(songTextInfoItem.getLink()).header("User-agent", USER_AGENT).get();
                            } catch (EOFException e) {
                                Logger.e(e, "Error get text from text page", Logger.SONG_INFO);
                            }
                            return null;
                        })
                                .filter(document -> document != null)
                                .map(document -> document.body().getElementsByClass("main-page").get(0).getElementsByClass("row").get(0).getAllElements().get(3).getAllElements().get(0))
                                .map(textElement -> {
                                    if (textElement != null) {
                                        return textElement;
                                    }
                                    Logger.w("Empty song text", Logger.SONG_INFO);
                                    return null;
                                })
                                .flatMap(element -> Observable.from(element.children().toArray(new Element[element.children().size()])))
                                .filter(element -> element.html().contains("<!-- Usage"))
                                .toList()
                                .filter(elementList -> elementList != null && !elementList.isEmpty())
                                .map(list -> {
                                    if (list != null && !list.isEmpty()) {
                                        return new SongTextItem(songTextInfoItem.getTitle(), Html.fromHtml(list.get(0).html()).toString());
                                    } else {
                                        Logger.w("Text element doesn't exist :(", Logger.SONG_INFO);
                                    }
                                    return null;
                                })

                )
                .filter(item -> item != null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<SongTextItem>() {
                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e, "Error of getting text");
                        EventBus.getDefault().post(new SongTextItemEvent(null, currentTrackInfo));
                        searchTextSubscription = null;
                    }

                    @Override
                    public void onNext(SongTextItem result) {
                        Logger.d("Song text is:" + result.getText());
                        EventBus.getDefault().post(new SongTextItemEvent(result, currentTrackInfo));
                        searchTextSubscription = null;
                    }
                });

    }
}
