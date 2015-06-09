package com.marche.moonlightembeddedcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.marche.moonlightembeddedcontroller.Adapter.GameAdapter;
import com.marche.moonlightembeddedcontroller.Events.GotGamesEvent;
import com.marche.moonlightembeddedcontroller.Events.SSHConnected;
import com.marche.moonlightembeddedcontroller.POJO.Container;
import com.marche.moonlightembeddedcontroller.POJO.Device;
import com.marche.moonlightembeddedcontroller.POJO.Result;
import com.marche.moonlightembeddedcontroller.RESTAPI.GamesAPIService;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;
import com.squareup.otto.Subscribe;

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
public class GameFragment extends Fragment {

    Device device;

    @InjectView(R.id.progressBar)
    CircleProgressBar loadingSpinner;

    @InjectView(R.id.listView)
    ListView listView;

    GameAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);
        ButterKnife.inject(this, rootView);

        Bundle bundle = this.getArguments();
        device = (Device) bundle.getSerializable("device");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(SSHManager.getInstance().isConnected){
            SSHManager.getInstance().getGames(getActivity());
        } else {
            SSHManager.getInstance().connectToSSH(getActivity(), device);
        }
    }

    @Subscribe
    public void SSHConnectedEvent(SSHConnected event){
        SSHManager.getInstance().getGames(getActivity());
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

    @OnItemClick(R.id.listView)
    public void onItemClick(int position) {
        SSHManager.getInstance().playGame(getActivity(), adapter.getItem(position).getName());
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

    public void displayGames(ArrayList<Result> games){
        loadingSpinner.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

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
