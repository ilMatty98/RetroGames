package com.nullpointerexception.retrogames.Snake;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.SaveScore;
import com.nullpointerexception.retrogames.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SnakePanelView extends View {
    private final static String TAG = SnakePanelView.class.getSimpleName();

    //È una lista di GridSquare usata per creare la mappa
    private List<List<GridSquare>> mGridSquare = new ArrayList<>();
    //Lista contenente le posizioni del serpente
    private List<GridPosition> mSnakePositions = new ArrayList<>();

    SaveScore game;

    //Listener per restituire score, highscore, e punteggio resettato
    private OnEatListener onEatListener;
    private OnResetListener onResetListener;

    //Istanza del thread principale


    private GridPosition mSnakeHeader;                         //posizione della testa del serpente
    private GridPosition mFoodPosition;                        //posizione del cibo
    private int mSnakeLength = 3;                              //lunghezza del serpente
    private int mDifficulty;
    private int mSpeed = 8;                                    //velocità del serpente
    private int mSnakeDirection = GameType.RIGHT;              //direzione iniziale serpente
    private boolean mIsEndGame = false;                        //Il gioco finisce
    private int mGridSize = 15;                                //Taglia della griglia
    private Paint mGridPaint = new Paint();                    //colore paint
    private Paint mStrokePaint = new Paint();                  //spessore paint
    private int mRectSize = dp2px(getContext(), 20);    //Dimensione del quadrato
    private int mStartX, mStartY;                              //cordinate posizione iniziale serpente
    private int mPoint;                                        //Segna il punteggio attuale
    private int mHighScore;                                    //Segna l'highscore attuale

    public SnakePanelView(Context context) {
        this(context, null);
    }

    public SnakePanelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        game= new SaveScore();
    }

    public SnakePanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Disegna la mappa per la prima volta
     */
    public void init() {
        List<GridSquare> squares;
        for (int i = 0; i < mGridSize; i++) {
            //Inserisce una lista di quadrati in ogni posizione di squares
            squares = new ArrayList<>();
            for (int j = 0; j < mGridSize; j++) {
                squares.add(new GridSquare(GameType.GRID));
            }
            mGridSquare.add(squares);
        }
        //Posiziona la testa
        mSnakeHeader = new GridPosition(10, 10);
        //Aggiunge nelle posizioni del serpente le coordinate della testa
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        //Imposta coordinate cibo iniziali
        mFoodPosition = new GridPosition(0, 0);
        mIsEndGame    = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mStartX = w / 2 - mGridSize * mRectSize / 2;
        mStartY = dp2px(getContext(), 1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mStartY * 2 + mGridSize * mRectSize;
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
    }

    /**
     * Disegna la grilia della mappa
     *
     * @param canvas Riceve in input un canvas inizializzato
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Pennello griglia
        mGridPaint.reset();
        mGridPaint.setStyle(Paint.Style.FILL);
        mGridPaint.setAntiAlias(true);

        mStrokePaint.reset();
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);

        for (int i = 0; i < mGridSize; i++) {
            for (int j = 0; j < mGridSize; j++) {
                int left = mStartX + i * mRectSize;
                int top = mStartY + j * mRectSize;
                int right = left + mRectSize;
                int bottom;
                if(j != (mGridSize-1))
                    bottom = top + mRectSize;
                else
                    bottom = top + mRectSize - 5;

                canvas.drawRect(left, top, right, bottom, mStrokePaint);
                mGridPaint.setColor(mGridSquare.get(i).get(j).getColor());
                canvas.drawRect(left, top, right, bottom, mGridPaint);
            }
        }
    }

    /**
     * Imposta la posizione del cibo
     *
     * @param foodPosition Riceve le coordinate del cibo
     */
    private void refreshFood(GridPosition foodPosition) {
        mGridSquare.get(foodPosition.getX()).get(foodPosition.getY()).setType(GameType.FOOD);
    }

    /**
     * Imposta la velocità del serpente
     *
     * @param speed Un intero che indica la velocità del serpente
     */
    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    /**
     * Imposta la difficoltà
     *
     * @param difficulty Un intero che indica la difficoltà
     */
    public void setDifficulty(int difficulty) {
        mDifficulty = difficulty;
    }

    /**
     * Imposta la dimensione della mappa
     *
     * @param gridSize Un intero che indica la grandezza della mappa
     */
    public void setGridSize(int gridSize) {
        mGridSize = gridSize;
    }

    /**
     * Fuznione che gestisce la direzione del serpente.
     * Se la posizione ricevuta è opposta a quella attuale allora non fa niente.
     *
     * @param snakeDirection Intero che indica la direzione del serpente
     */
    public void setSnakeDirection(int snakeDirection) {
        if (mSnakeDirection == GameType.RIGHT && snakeDirection == GameType.LEFT) return;
        if (mSnakeDirection == GameType.LEFT && snakeDirection == GameType.RIGHT) return;
        if (mSnakeDirection == GameType.TOP && snakeDirection == GameType.BOTTOM) return;
        if (mSnakeDirection == GameType.BOTTOM && snakeDirection == GameType.TOP) return;
        mSnakeDirection = snakeDirection;
    }

    /**
     * Thread principale del gioco
     */
    private class GameMainThread extends Thread {

        @Override
        public void run() {
            while (!mIsEndGame) {
                moveSnake(mSnakeDirection);
                checkCollision();
                refreshGridSquare();
                handleSnakeTail();
                postInvalidate();     //Ridisegna l'interfaccia
                handleSpeed();
            }
        }

        /**
         * Gestisce la velocità di aggiornamento
         */
        private void handleSpeed() {
            try {
                sleep(1000 / mSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Quando la testa si incontra con un altro elemento del corpo
     * si ha una collisione e si perde la partita.
     *
     * Gestisce anche le collisioni con il cibo chiamando la funzione
     * addPoint() e generateFood()
     */
    private void checkCollision() {
        //Ottiene la posizione della testa
        GridPosition headerPosition = mSnakePositions.get(mSnakePositions.size() - 1);

        for (int i = 0; i < mSnakePositions.size() - 2; i++) {
            GridPosition position = mSnakePositions.get(i);
            if (headerPosition.getX() == position.getX() && headerPosition.getY() == position.getY()) {
                //Il serpente si è morso
                mIsEndGame = true;
                showMessageDialog();
                return;
            }
        }

        //Rileva collisioni cibo
        if (headerPosition.getX() == mFoodPosition.getX()
                && headerPosition.getY() == mFoodPosition.getY()) {   //Se la posizione della testa
            mSnakeLength++;                                           //è uguale a quella del cibo
            generateFood();
            addPoint();                                               //aumenta la lunghezza e
        }                                                             //genera il nuovo cibo
    }

    /**
     * Aggiunge un punto allo score e se si super l'Highscore
     * lo si carica sul DB
     */
    private void addPoint() {
        mPoint ++;
        if(mPoint > mHighScore) {
            mHighScore = mPoint;
            game.save(App.SNAKE, mPoint, getContext());
        }
        if(onEatListener != null)
            onEatListener.onEat(mPoint, mHighScore);
    }

    /**
     * Mostra il dialog quando si perde la partita per scegliere se ricominciarla
     * o terminarla
     */
    private void showMessageDialog() {
        post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(getContext()).setTitle(getResources().getString(R.string.gameOver))
                        .setCancelable(false)
                        .setMessage(getResources().getString(R.string.your_score_is) + " " + mPoint)
                        .setPositiveButton(getResources().getString(R.string.again), (dialog, which) -> {
                            dialog.dismiss();
                            mPoint = 0;
                            if(onResetListener != null)
                                onResetListener.onReset(mPoint);
                            startGame(GameType.GENERIC_DIFFICULTY, false);
                        })
                        .setNegativeButton(getResources().getString(R.string.exit), (dialog, which) -> {
                            dialog.dismiss();
                            if(getContext() instanceof MainActivitySnake)
                                ((MainActivitySnake) getContext()).finish();
                        })
                        .create()
                        .show();
            }
        });
    }

    /**
     * Funzione che gestisce l'avvio della partita
     *
     * @param difficulty Un intero passato per comunicare la difficoltà
     * @param isRestart Indica se la partita deve essere riavviata o è il primo avvio
     */
    public void startGame(int difficulty, boolean isRestart) {
        //In base alla difficoltà, cambia la velocità del serpente
        if(!isRestart) {
            switch (difficulty) {
                case GameType.EASY:
                    setDifficulty(GameType.EASY);
                    setSpeed(4);
                    break;
                case GameType.MEDIUM:
                    setDifficulty(GameType.MEDIUM);
                    setSpeed(8);
                    break;
                case GameType.HARD:
                    setDifficulty(GameType.HARD);
                    setSpeed(12);
                    break;
            }
        }

        //Legge dal database se esistono salvataggi del gioco
        if(App.scoreboardDao.getGame(App.SNAKE) != null)
            mHighScore = App.scoreboardDao.getScore(App.SNAKE);  //se si, l'highscore viene aggiornato
        else
            mHighScore = 0;

        //if (!mIsEndGame) return;
        for (List<GridSquare> squares : mGridSquare) {
            for (GridSquare square : squares) {
                square.setType(GameType.GRID);
            }
        }
        if (mSnakeHeader != null) {
            mSnakeHeader.setX(10);
            mSnakeHeader.setY(10);
        } else
            mSnakeHeader = new GridPosition(10, 10);    //The initial position of the snake

        mSnakePositions.clear();
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        mSnakeLength    = 3;                          //Lunghezza serpente
        mSnakeDirection = GameType.RIGHT;

        if (mFoodPosition != null)
            generateFood();

        mIsEndGame = false;
        if(!isRestart) {
            GameMainThread thread = new GameMainThread();
            thread.start();
        }
        else {
            mPoint = 0;
            if(onResetListener != null)
                onResetListener.onReset(mPoint);
        }
    }

    /**
     * Genera il cibo
     */
    private void generateFood() {
        Random random = new Random();
        int foodX = random.nextInt(mGridSize - 1);
        int foodY = random.nextInt(mGridSize - 1);
        for (int i = 0; i < mSnakePositions.size() - 1; i++) {
            //Se il cibo si genera sulla posizione del corpo, lo rigenera
            if (foodX == mSnakePositions.get(i).getX() && foodY == mSnakePositions.get(i).getY()) {

                foodX = random.nextInt(mGridSize - 1);
                foodY = random.nextInt(mGridSize - 1);
                //Resetta il contatore
                i = 0;
            }
        }
        mFoodPosition.setX(foodX);
        mFoodPosition.setY(foodY);
        refreshFood(mFoodPosition);
    }

    /**
     * Controlla la posizione della testa del serpente
     * Se la testa del serpente raggiunge il bordo della mappa allora dovrà spostarsi
     * dalla parte opposta della mappa.
     * inoltre memorizza la posizione della tasta nelle posizioni del corpo
     *
     * @param snakeDirection Un intero che indica la direzione del serpente
     */
    private void moveSnake(int snakeDirection) {
        switch (snakeDirection) {
            case GameType.LEFT:
                //se raggiunge l'estrema sinistra, attraversa lo schermo all'estrema destra
                if (mSnakeHeader.getX() - 1 < 0) {
                    mSnakeHeader.setX(mGridSize - 1);
                } else {
                    mSnakeHeader.setX(mSnakeHeader.getX() - 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
            case GameType.TOP:
                //se raggiunge l'estremo superiore, attraversa lo schermo dal basso
                if (mSnakeHeader.getY() - 1 < 0) {
                    mSnakeHeader.setY(mGridSize - 1);
                } else {
                    mSnakeHeader.setY(mSnakeHeader.getY() - 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
            //se raggiunge l'estrema destra, attraversa lo schermo all'estrema sinistra
            case GameType.RIGHT:
                if (mSnakeHeader.getX() + 1 >= mGridSize) {
                    mSnakeHeader.setX(0);
                } else {
                    mSnakeHeader.setX(mSnakeHeader.getX() + 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
            //se raggiunge l'estrema sinistra, attraversa lo schermo all'estrema destra
            case GameType.BOTTOM:
                if (mSnakeHeader.getY() + 1 >= mGridSize) {
                    mSnakeHeader.setY(0);
                } else {
                    mSnakeHeader.setY(mSnakeHeader.getY() + 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
        }
    }

    /**
     * controlla tutta la griglia della mappa e aggiorna le posizioni del serpente
     */
    private void refreshGridSquare() {
        for (GridPosition position : mSnakePositions)
            mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.SNAKE);
    }

    /**
     * Gestisce la coda del serpente.
     *
     * Imposta ogni quadrato della mappa in tipo mappa se il serpente non è su di essi.
     */
    private void handleSnakeTail() {
        int snakeLength = mSnakeLength;
        for (int i = mSnakePositions.size() - 1; i >= 0; i--) {
            if (snakeLength > 0) {
                snakeLength--;
            } else {
                GridPosition position = mSnakePositions.get(i);
                mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.GRID);
            }
        }
        snakeLength = mSnakeLength;
        for (int i = mSnakePositions.size() - 1; i >= 0; i--) {
            if (snakeLength > 0) {
                snakeLength--;
            } else {
                mSnakePositions.remove(i);
            }
        }
    }

    /**
     * Converte la dimensione dei dp in pixels
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

    public void setOnEatListener(OnEatListener onEatListener) {
        this.onEatListener = onEatListener;
    }

    public void setOnResetListener(OnResetListener onResetListener) {
        this.onResetListener = onResetListener;
    }

    public interface OnEatListener {
        void onEat(int point, int highScore);
    }

    public interface OnResetListener {
        void onReset(int point);
    }
}
