package com.nullpointerexception.retrogames.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Breakout.MainActivityBreakout;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Pacman.MainActivityPacman;
import com.nullpointerexception.retrogames.Pong.MainActivityPong;
import com.nullpointerexception.retrogames.R;
import com.nullpointerexception.retrogames.SpaceInvaders.MainActivitySpaceInvaders;
import com.nullpointerexception.retrogames.Tetris.Tetris;

public class GamesFragment extends Fragment
{

    /*
           UI Components
     */
    private ViewGroup tetrisCard, pacmanCard, pongCard, spaceInvadersCard, breakoutCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tetrisCard = view.findViewById(R.id.CardView_tetris);
        pacmanCard = view.findViewById(R.id.CardView_pacman);
        pongCard = view.findViewById(R.id.CardView_pong);
        spaceInvadersCard = view.findViewById(R.id.CardView_spaceinvaders);
        breakoutCard = view.findViewById(R.id.CardView_brick_break);

        tetrisCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), Tetris.class);
                startActivity(intent);
            }
        });

        pacmanCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), MainActivityPacman.class);
                startActivity(intent);
            }
        });

        pongCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), MainActivityPong.class);
                startActivity(intent);
            }
        });

        spaceInvadersCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), MainActivitySpaceInvaders.class);
                startActivity(intent);
            }
        });

        breakoutCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), MainActivityBreakout.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
