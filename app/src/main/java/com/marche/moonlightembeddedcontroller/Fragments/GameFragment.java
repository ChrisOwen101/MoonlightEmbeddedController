package com.marche.moonlightembeddedcontroller.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.marche.moonlightembeddedcontroller.Adapter.GameAdapter;
import com.marche.moonlightembeddedcontroller.Events.GameLoadingEvent;
import com.marche.moonlightembeddedcontroller.Events.GotGamesEvent;
import com.marche.moonlightembeddedcontroller.Events.RefreshGames;
import com.marche.moonlightembeddedcontroller.Events.SSHConnected;
import com.marche.moonlightembeddedcontroller.MainActivity;
import com.marche.moonlightembeddedcontroller.POJO.Container;
import com.marche.moonlightembeddedcontroller.POJO.Device;
import com.marche.moonlightembeddedcontroller.POJO.Result;
import com.marche.moonlightembeddedcontroller.R;
import com.marche.moonlightembeddedcontroller.RESTAPI.GamesAPIService;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;
import com.squareup.otto.Subscribe;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.SlideInEffect;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class GameFragment extends Fragment implements AbsListView.OnScrollListener{

    Device device;

    @InjectView(R.id.progressBar)
    CircleProgressBar loadingSpinner;

    @InjectView(R.id.listView)
    JazzyListView listView;

    GameAdapter adapter;
    MaterialDialog gameLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);
        ButterKnife.inject(this, rootView);

        listView.setTransitionEffect(new SlideInEffect());
        listView.setOnScrollListener(this);


        Bundle bundle = this.getArguments();
        device = (Device) bundle.getSerializable("device");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(SSHManager.getInstance().isConnected){
            loadGames();
        } else {
            SSHManager.getInstance().connectToSSH(getActivity(), device);
        }
    }

    public void loadGames(){
        String games = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("games", "");

        if(games.isEmpty()){
            SSHManager.getInstance().getGames(getActivity());
        } else {
            Type typeOfT = new TypeToken<ArrayList<Result>>(){}.getType();
            ArrayList<Result> results = new Gson().fromJson(games, typeOfT );
            displayGames(results);
        }
    }

    @Subscribe
    public void RefreshGamesEvent(RefreshGames event){
        loadingSpinner.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        SSHManager.getInstance().getGames(getActivity());
    }

    @Subscribe
    public void SSHConnectedEvent(SSHConnected event){
        loadGames();
    }

    @Subscribe
    public void GotGamesEvent(GotGamesEvent event){
        ArrayList<String> gameNames = new ArrayList<>();

        for(String names : event.gameNames){
            String lines[] = names.split("\\r?\\n");
            gameNames.addAll(Arrays.asList(lines));
        }

        queryGames(gameNames);
    }

    @Subscribe
    public void GameLoadingEvent(GameLoadingEvent event){
        if(gameLoading != null){
            if(event.done){
                gameLoading.dismiss();
                gameLoading = null;
            } else {
                gameLoading.setContent(event.text);
            }
        }
    }

    int mLastFirstVisibleItem = 0;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {   }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getId() == listView.getId()) {
            final int currentFirstVisibleItem = listView.getFirstVisiblePosition();

            if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                ((MainActivity)getActivity()).getSupportActionBar().hide();
            } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                ((MainActivity)getActivity()).getSupportActionBar().show();
            }

            mLastFirstVisibleItem = currentFirstVisibleItem;
        }
    }

    @OnItemClick(R.id.listView)
    public void onItemClick(int position) {
        SSHManager.getInstance().playGame(getActivity(), adapter.getItem(position).getName());

        gameLoading = new MaterialDialog.Builder(getActivity())
                .title("Starting " + adapter.getItem(position).getName())
                .content("This'll just take a second...")
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    public void queryGames(final ArrayList<String> gameNames ){
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://www.giantbomb.com/api")
                .setConverter(new GsonConverter(gson))
                .build();

        GamesAPIService service = restAdapter.create(GamesAPIService.class);

        final ArrayList<Result> game = new ArrayList<>();

        for(String gameName : gameNames){
            if(gameName.equals("Steam")){
                Result r = new Result();
                r.setName("Steam");
                game.add(r);
            } else {
                service.searchForGame("2625851d1a8f443018012f69e58db1474c62bb1d",
                        "json" ,
                        gameName,
                        "game",
                        1,
                        new Callback<Container>() {
                            @Override
                            public void success(Container container, Response response) {
                                game.add(container.getResults().get(0));

                                if(game.size() == gameNames.size()){
                                    displayGames(game);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
            }
        }
    }

    public void displayGames(ArrayList<Result> games){
        loadingSpinner.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        String jsonGames = new Gson().toJson(games);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString("games", jsonGames);
        editor.commit();

        adapter = new GameAdapter(getActivity(),R.layout.game_list, games);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        SSHManager.getInstance().SSHBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SSHManager.getInstance().SSHBus.unregister(this);
    }
}
