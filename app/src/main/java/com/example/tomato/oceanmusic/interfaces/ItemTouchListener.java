package com.example.tomato.oceanmusic.interfaces;

public interface ItemTouchListener {
    void onMove(int oldPosition, int newPosition);

    void swipe(int position, int direction);
}
