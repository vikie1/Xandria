package com.xandria.tech.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.xandria.tech.model.BookRecyclerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class GoogleServices {
    public static final String QUERY_API_LINK = "https://www.googleapis.com/books/v1/volumes?q=";

    public static List<BookRecyclerModel> searchBook(String query) throws IOException {
        URL url = new URL(QUERY_API_LINK + query + "&maxResults=10"); // i don't think we need more than the first 10 results

        HttpsURLConnection apiConnection = (HttpsURLConnection) url.openConnection();
        if (apiConnection.getResponseCode() == 200){ // the ok response from a server
            InputStream responseBody = apiConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseBody, StandardCharsets.UTF_8)); // make response body a readable json
            StringBuilder stringBuilder = new StringBuilder();
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null){
                stringBuilder.append(currentLine);
            }
            try {
                return parseBookRecyclerModelList(new JSONObject(stringBuilder.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private static List<BookRecyclerModel> parseBookRecyclerModelList(JSONObject jsonObject) {
        List<BookRecyclerModel> googleBooksList = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentBook;
            try {
                currentBook = jsonArray.getJSONObject(i).getJSONObject("volumeInfo");
            } catch (JSONException e) {
                e.printStackTrace();
                break; // get out of the loop as we have no book data
            }
            BookRecyclerModel book = new BookRecyclerModel();

            /*
             the reason for all this individual try catch statements is because
             a book might lack one property but have the other, e.g lack a subtitle but have a description
             such a book can be saved. If we put all in one try catch then if a book lacks, say, the subtitle,
             then it would throw an exception and nothing in the try statement will be saved.
             Separating them means that a missing field will just be populated as null without affecting the rest of the fields.
             However, no book should lack a title. Books with no title won't be added to the list.
             */
            try {
                book.setTitle(currentBook.getString("title"));
                book.setBookID(book.getTitle().replaceAll("[\\-+.^:,]","_"));
            } catch (JSONException e){
                e.printStackTrace();
                continue; // books missing the title should just be skipped
            }
            try {
                book.setAuthors(currentBook.getJSONArray("authors").join(","));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                book.setSubtitle(currentBook.getString("subtitle"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                book.setPublisher(currentBook.getString("publisher"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                book.setPublishedDate(currentBook.getString("publishedDate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                book.setDescription(currentBook.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                book.setPageCount(currentBook.getString("pageCount"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                book.setThumbnail(currentBook.getJSONObject("imageLinks").getString("thumbnail"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                book.setPreviewLink(currentBook.getString("previewLink"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                book.setISBN(currentBook.getJSONArray("industryIdentifiers").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                book.setCategory(currentBook.getJSONArray("categories").join(","));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            googleBooksList.add(book);
        }
        return googleBooksList;
    }

    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }

}
