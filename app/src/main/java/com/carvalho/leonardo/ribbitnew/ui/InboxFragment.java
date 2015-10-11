package com.carvalho.leonardo.ribbitnew.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.carvalho.leonardo.ribbitnew.adapters.MessageAdapter;
import com.carvalho.leonardo.ribbitnew.utils.ParseConstants;
import com.carvalho.leonardo.ribbitnew.R;
import com.carvalho.leonardo.ribbitnew.utils.ToastGen;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonardo on 02/10/2015.
 */
public class InboxFragment extends ListFragment
{

    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

       mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
       mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
       mSwipeRefreshLayout.setColorSchemeColors(
               R.color.swipeRefresh1,
               R.color.swipeRefresh2,
               R.color.swipeRefresh3,
               R.color.swipeRefresh4
               );

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //setar uma barra de progresso visível aqui

        retrieveMessages();

    }

    private void retrieveMessages() {
        ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENTS_IDS, ParseUser.getCurrentUser().getObjectId());
        //query.addAscendingOrder(ParseConstants.KEY_CREATED_AT);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e)
            {
                //setar uma barra de progresso invisível aqui

                if(mSwipeRefreshLayout.isRefreshing())
                {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if(e == null)
                {
                    //We found messages
                    mMessages = messages;

                    //Setei pra remover os dividers na fragment_inbox.xml
                    //getListView().setDividerHeight(0);
                    //getListView().setDivider(null);

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;

                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }

                    if(getListView().getAdapter() == null)
                    {
                        //se o adapter ainda não está setado guarda os resultados dentro de um array adapter
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);

                        //captura a lista pra fazer a população através do adapter e setar o onclick dos itens
                        //mLista = (ListView) findViewById(R.id.lista);

                        setListAdapter(adapter);
                    }
                    else
                    {
                        //se o adapter já está setado só faz o preenchimento denovo com o mesmo que já tem
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }


                }

            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);

        Uri fileUri = Uri.parse(file.getUrl());

        if(messageType.equals(ParseConstants.TYPE_IMAGE))
        {
            //view image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);

        }
        else
        {
            //view video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);

        }

        //Delete it!
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENTS_IDS);

        if(ids.size() == 1)
        {
            //last recipient delete the whole thing
            message.deleteInBackground();
            Log.d("deletamento", "entrou aqui");
        }
        else
        {
            //remove the recipient and save
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstants.KEY_RECIPIENTS_IDS, idsToRemove);

            message.saveInBackground();

        }

    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh()
        {
            retrieveMessages();
        }
    };


}
