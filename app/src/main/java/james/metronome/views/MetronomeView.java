package james.metronome.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.afollestad.aesthetic.Aesthetic;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MetronomeView extends View {

    private Paint paint;
    private long interval = 500;
    private float distance;

    private Disposable subscription;

    public MetronomeView(Context context) {
        this(context, null);
    }

    public MetronomeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MetronomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        subscribe();
    }

    public void subscribe() {
        subscription = Aesthetic.get()
                .textColorPrimary()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) {
                        paint.setColor(integer);
                    }
                });
    }

    public void unsubscribe() {
        subscription.dispose();
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void onTick() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(interval);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                distance = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAlpha((int) (255 * (1 - distance)));
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, distance * Math.max(canvas.getWidth(), canvas.getHeight()) / 2, paint);
    }
}
