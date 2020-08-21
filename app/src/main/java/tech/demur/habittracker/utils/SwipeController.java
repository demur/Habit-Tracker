package tech.demur.habittracker.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import tech.demur.habittracker.R;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static io.realm.Realm.getApplicationContext;

enum ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

public class SwipeController extends ItemTouchHelper.Callback {

    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private RectF buttonInstance = null;
    private RectF leftBtnInstance = null;
    private RectF rightBtnInstance = null;
    private RecyclerView.ViewHolder currentItemViewHolder = null;
    private SwipeControllerActions buttonsActions = null;
    private static final float iconScale = 0.5f;
    private static final float buttonCorners = 16;
    private static final int buttonPadding = 8;

    public SwipeController(SwipeControllerActions buttonsActions) {
        this.buttonsActions = buttonsActions;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT);// | RIGHT
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int buttonSide = viewHolder.itemView.getBottom() - viewHolder.itemView.getTop();
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                // if (buttonShowedState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, (buttonSide + buttonPadding));
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE)
                    dX = Math.min(dX, -2 * (buttonSide + buttonPadding));
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder;
    }

    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int buttonSide = viewHolder.itemView.getBottom() - viewHolder.itemView.getTop();
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    if (dX < -2 * (buttonSide + buttonPadding))
                        buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                    // else if (dX > (buttonSide + buttonPadding)) buttonShowedState = ButtonsState.LEFT_VISIBLE;

                    if (buttonShowedState != ButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    swipeBack = false;


                    /*if (buttonsActions != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
                        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                            buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
                        } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                            buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
                        }
                    }*/
                    if (buttonsActions != null) {
                        if (null != leftBtnInstance && leftBtnInstance.contains(event.getX(), event.getY())) {
                            buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
                        } else if (null != rightBtnInstance && rightBtnInstance.contains(event.getX(), event.getY())) {
                            buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
                        }
                    }


                    buttonShowedState = ButtonsState.GONE;
                    currentItemViewHolder = null;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        View itemView = viewHolder.itemView;
        Paint p = new Paint();
        int buttonSide = itemView.getBottom() - itemView.getTop();
        int iconOffset = (int) (buttonSide * (1 - iconScale) / 2);

        int btn_right = itemView.getRight() - 2 * buttonSide - buttonPadding;
        int btn_top = itemView.getTop();
        int btn_left = itemView.getRight() - buttonSide - buttonPadding;
        int btn_bottom = itemView.getBottom();
        RectF leftButton = new RectF(btn_right, btn_top,
                btn_left, btn_bottom);
        p.setColor(Color.rgb(255, 165, 0));
        c.drawRoundRect(leftButton, buttonCorners, buttonCorners, p);
        Drawable iconEdit = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_edit);
        if (iconEdit != null) {
            iconEdit.setBounds(btn_right + iconOffset, btn_top + iconOffset,
                    btn_left - iconOffset, btn_bottom - iconOffset);
            iconEdit.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            iconEdit.draw(c);
        }

        btn_right = itemView.getRight() - buttonSide;
        btn_top = itemView.getTop();
        btn_left = itemView.getRight();
        btn_bottom = itemView.getBottom();
        RectF rightButton = new RectF(btn_right, btn_top,
                btn_left, btn_bottom);
        p.setColor(Color.RED);
        c.drawRoundRect(rightButton, buttonCorners, buttonCorners, p);
        Drawable iconDelete = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete);
        if (iconDelete != null) {
            iconDelete.setBounds(btn_right + iconOffset, btn_top + iconOffset,
                    btn_left - iconOffset, btn_bottom - iconOffset);
            iconDelete.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            iconDelete.draw(c);
        }

        leftBtnInstance = leftButton;
        rightBtnInstance = rightButton;
        /*buttonInstance = null;
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton;
        } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton;
        }*/
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
    }

    public void onDraw(Canvas c) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder);
        }
    }
}