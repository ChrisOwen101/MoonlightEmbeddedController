package com.marche.moonlightembeddedcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
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

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class GameFragment extends Fragment {

    Device device;

    @InjectView(R.id.progressBar)
    CircleProgressBar loadingSpinner;

    @InjectView(R.id.listView)
    ListView listView;

    public ArrayList<String> gameNames = new ArrayList<>();

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
        loadingSpinner.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        for(String names : event.gameNames){
            String lines[] = names.split("\\r?\\n");
            gameNames.addAll(Arrays.asList(lines));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, gameNames);
        listView.setAdapter(adapter);

        //queryGames(gameNames);
    }

    @OnItemClick(R.id.listView)
    public void onItemClick(int position) {
        SSHManager.getInstance().playGame(getActivity(), gameNames.get(position));
    }

    public void queryGames(final ArrayList<String> gameNames ){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://www.giantbomb.com/api")
                .build();

        GamesAPIService service = restAdapter.create(GamesAPIService.class);

        final ArrayList<Result> game = new ArrayList<>();

        for(String gameName : gameNames){
            service.searchForGame(gameName, new Callback<Container>() {
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
        for(Result game: games){
            System.out.println(game.getDescription());
        }
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
