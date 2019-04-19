import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class MutilTaskJoinTest {

    public static void main(String[] args) {
        long currTime = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask task = new CountTask(1, 100);
        Future<Integer> future = forkJoinPool.submit(task);
        try {
            System.out.println(future.get());
            System.out.println("用时毫秒数：" + (System.currentTimeMillis() - currTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static class CountTask extends RecursiveTask<Integer> {


        private static final long serialVersionUID = -4488036422261690638L;

        private static final int THRESHOLD = 10;
        private int start;
        private int end;

        public CountTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            int sum = 0;
           //假设任务足够小就计算任务
            boolean canCompute = (end - start) <= THRESHOLD;
            if (canCompute) {
                for (int i = start; i <= end; i++) {
                    sum += i;
                }
            } else {
                //假设任务大于阀值，就分裂成两个子任务计算
                int middle = (start + end) / 2;
                CountTask leftTask = new CountTask(start, middle);
                CountTask rightTask = new CountTask(middle + 1, end);
                leftTask.fork();
                rightTask.fork();
                //等待子任务运行完，并得到其结果
                int leftResult = leftTask.join();
                int rightResult = rightTask.join();
                sum = leftResult + rightResult;
            }
            return sum;
        }


    }
}
