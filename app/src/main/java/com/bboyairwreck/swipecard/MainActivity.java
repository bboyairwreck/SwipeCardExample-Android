package com.bboyairwreck.swipecard;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private ViewGroup _root;
    private ImageButton btnNope;
    private ImageButton btnYeah;
    private ImageButton btnClear;
    private int numCards;

    ImageView imageView;
    private int screenWidth = 0;    // width of rootView
    private int screenHeight = 0;
    private float pivotX;           // rotation pivot X position
    private float pivotY;           // rotation pivot Y position
    private int _xDelta;            // distance card dragged in X-axis

    public static final float MAX_ROTATION = 17f;   // max degrees to rotate card
    public static final int MAX_CARDS = 7;         // max number of cards on screen at a time
    public static final int MIN_CARDS = 3;         // min number of cards on screen at a time
    public static final float GO_LEFT = -1000f;   // max degrees to rotate card
    public static final float GO_RIGHT = 1000f;   // max degrees to rotate card
    public static final int cardMargin = 67;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get Root container
        _root = (ViewGroup) findViewById(R.id.root);

        int buttonSize = 100;
        btnNope = (ImageButton) findViewById(R.id.btnNope);
        btnYeah = (ImageButton) findViewById(R.id.btnYeah);
        btnClear = (ImageButton) findViewById(R.id.btnClear);

        btnNope.setImageBitmap(
                SwipeCardApp.decodeSampledBitmapFromResource(getResources(), R.drawable.nope, buttonSize, buttonSize));
        btnYeah.setImageBitmap(
                SwipeCardApp.decodeSampledBitmapFromResource(getResources(), R.drawable.yeah, buttonSize, buttonSize));


        this.numCards = MAX_CARDS;
    }

    /*
     * Since cannot get root width onCreate because view hasn't loaded by then, get width here.
     * Add max num or cards to root view
     */
    @Override
    public void onWindowFocusChanged(boolean b) {
        this.screenHeight = findViewById(R.id.contentMainContainer).getHeight();
        this.screenWidth = _root.getWidth();
        addCards(MAX_CARDS);

        btnNope.setOnClickListener(decisionButtonListener(GO_LEFT));
        btnYeah.setOnClickListener(decisionButtonListener(GO_RIGHT));
    }

    private void addCards(int size) {
        int cardWidth = this.screenWidth - (cardMargin * 2);    // width of each card
        int leftMargin = cardMargin;

        int bottomButtonsHeight = (int) getResources().getDimension(R.dimen.yeahNope_plus_results);
        int maxWidth = screenHeight - (bottomButtonsHeight + cardMargin);
        if (cardWidth > maxWidth) {
            cardWidth = maxWidth;
            leftMargin = (screenWidth - cardWidth) / 2;
        }

        // Create cards in add to root view
        for (int i = 0; i < size; i++) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout card = (RelativeLayout) vi.inflate(R.layout.card_details, null); // ignore this warning because we are adding it at the end

            // TODO Change these values to the image/Title in the card
            int cardDrawableID = R.drawable.person_placeholder;
            String cardTitle = "Card Title";

            imageView = (ImageView) card.findViewById(R.id.ivFoodImage);

            // Set different ID per image card
            imageView.setImageBitmap(
                    SwipeCardApp.decodeSampledBitmapFromResource(getResources(), cardDrawableID, 100, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Set Image width & height the size of the card
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(cardWidth, cardWidth));

            // Set Title of card
            TextView tvFoodTitle = (TextView) card.findViewById(R.id.tvFoodTitle);
            tvFoodTitle.setText(cardTitle);

            // Change this is height of card is different than width
            int height = cardWidth;

            // Set Card width & height. Add left & top margin to card.
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(cardWidth, height); // width & height
            layoutParams.leftMargin = leftMargin;
            layoutParams.topMargin = cardMargin;
            card.setLayoutParams(layoutParams);

            // TODO Delete. Each card alternates between Green & RED
            card.setBackgroundColor(Color.RED);
            if (i % 2 == 0) {
                card.setBackgroundColor(Color.GREEN);
            }
            // TODO DELETE ^

            this.pivotX = cardWidth / 2;    // set x pivot point center
            this.pivotY = height * 2f;      // set y pivot point 2x below the height

            // set pivot location to bottom center
            card.setPivotX(pivotX);
            card.setPivotY(pivotY);

            // Add card to root view & set onTouchListener
            _root.addView(card, 0, layoutParams);
            card.setOnTouchListener(this);
        }
    }

    /*
     * If detecting card swipe, this method will determine if current card should be removed or reset
     */
    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        final int X = (int) event.getRawX();    // X position of touch event
        int dif;                                // temp xDelta
        float swipePercent;                     // % of distance from center X to edge of screen

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // On Press Down
            case MotionEvent.ACTION_DOWN:
                _xDelta = X - (int)v.getTranslationX();    // track how far moved x
                break;
            // On Drag
            case MotionEvent.ACTION_MOVE:
                dif = X - _xDelta;
                swipePercent = (float)dif / (float)screenWidth;

                float rotation = swipePercent * MAX_ROTATION;
                float alpha = (1f - Math.abs(swipePercent));

                // set rotation, alpha, & translation
                v.setAlpha(alpha);
                v.setRotation(rotation);
                v.setTranslationX(dif);

                Log.d("OnTouch", "swipePercent = " + swipePercent + "; rotation = " + rotation + "; alpha = " + alpha);
                break;
            // On Release
            case MotionEvent.ACTION_UP:
                dif = X - _xDelta;
                swipePercent = Math.abs((float) dif / (float) screenWidth);

                // Reset card position if did NOT reach swipe threshold
                if (swipePercent < 0.3f) {
                    v.setAlpha(1);
                    v.setRotation(0f);
                    v.setTranslationX(0);

                    // Animate card off screen if did reach swipe threshold
                } else {
                    animateCardOff(v , v.getRotation(), 600);
                }

                Log.i("OnTouch", "Touch Released");

                break;
        }
        _root.invalidate();
        return true;
    }

    private View.OnClickListener decisionButtonListener(final float direction){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View card = null;
                for (int i = _root.getChildCount()-1; i >= 0; i--) {
                    View curView = _root.getChildAt(i);

                    if (curView != null && curView.getTag() != null && curView.getTag().toString().equals("card")) {
                        card = curView;
                        break;
                    }
                }
//                _root.getChildAt(_root.getChildCount());
//                Log.i("ViewName",card.getClass().getName());

                if (card != null) {
                    animateCardOff(card, direction, 400);
                }
            }
        };
    }

    private void animateCardOff(final View v, float rotation, int animDuration){
        float sign = 1;
        if (rotation < 0) {
            sign = -1;
        } else {
            // If Liked card, add card to database
            // TODO add card to database
        }
        RotateAnimation rotateAnim = new RotateAnimation(0, MAX_ROTATION*sign, pivotX, pivotY);
        rotateAnim.setDuration(animDuration);
        rotateAnim.setRepeatCount(0);

        TranslateAnimation translateAnim = new TranslateAnimation(0, (this.screenWidth)*sign, 0, 0);
        translateAnim.setDuration(animDuration);
        translateAnim.setRepeatCount(0);

        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(rotateAnim);
        animSet.addAnimation(translateAnim);
        animSet.setFillAfter(true);
        animSet.setAnimationListener(new CardAnimationListener(v, _root, btnYeah, btnNope, new Runnable() {
            @Override
            public void run() {
                // it works without the runOnUiThread, but all UI updates must be done on the UI thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        _root.removeView(v);        // removes card from root view
                        Log.i("onAnimationEnd", "Card removed");

                        btnNope.setEnabled(true);
                        btnYeah.setEnabled(true);

                        MainActivity.this.numCards--; // decrement number of cards

                        // If down to MIN_CARDS, add more cards
                        if (numCards <= MIN_CARDS) {
                            int numOfAddedCards = MAX_CARDS - numCards;
                            MainActivity.this.addCards(numOfAddedCards);
                            MainActivity.this.numCards += numOfAddedCards;

                            Log.i("onAnimationEnd", "Added " + numOfAddedCards + " cards to root view");
                        }
                    }
                });
            }
        }));

        // Animate card off screen and remove it
        v.startAnimation(animSet);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
        imageView.setImageBitmap(null);
        btnNope.setImageBitmap(null);
        btnYeah.setImageBitmap(null);
        btnClear.setImageBitmap(null);
    }

}
