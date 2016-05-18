package com.example.yanjiang.stockchart.rxutils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 小鄧子:【译】避免打断链式结构：使用.compose( )操作符 http://www.jianshu.com/p/e9e03194199e
 * Created by Joker on 2015/8/10.
 */
public class SchedulersCompat {

    private static final Observable.Transformer computationTransformer =
            new Observable.Transformer() {
                @Override
                public Object call(Object observable) {
                    return ((Observable) observable).subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread());
                }
            };

    private static final Observable.Transformer ioTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };
    private static final Observable.Transformer newTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };
    private static final Observable.Transformer trampolineTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.trampoline())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final Observable.Transformer executorTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.from(ExecutorManager.eventExecutor))
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * Don't break the chain: use RxJava's compose() operator
     */
    public static <T> Observable.Transformer<T, T> applyComputationSchedulers() {

        return (Observable.Transformer<T, T>) computationTransformer;
    }

    public static <T> Observable.Transformer<T, T> applyIoSchedulers() {

        return (Observable.Transformer<T, T>) ioTransformer;
    }

    public static <T> Observable.Transformer<T, T> applyNewSchedulers() {

        return (Observable.Transformer<T, T>) newTransformer;
    }

    public static <T> Observable.Transformer<T, T> applyTrampolineSchedulers() {

        return (Observable.Transformer<T, T>) trampolineTransformer;
    }

    public static <T> Observable.Transformer<T, T> applyExecutorSchedulers() {

        return (Observable.Transformer<T, T>) executorTransformer;
    }

  /*  *//**
     * 显示并隐藏loading
     *//*
    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> showLoading(final TransActivity activity) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        activity.showLoadingDialog();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        activity.hideLoadingDialog();
                    }
                }).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }*/
}
