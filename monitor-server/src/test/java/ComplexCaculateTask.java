import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;


public class ComplexCaculateTask extends RecursiveTask<Integer> {
    private static final long serialVersionUID = -4068008344384908582L;
    private Integer max_limit = 3;
    private Integer start;
    private Integer end;

    public ComplexCaculateTask(Integer start, Integer end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        Integer sum = 0;
        if (start - end <= max_limit) {
            for (int i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            //任务拆分2分
            int middle = (end + start) / 2;
            ComplexCaculateTask leftTask = new ComplexCaculateTask(start, middle);
            ComplexCaculateTask rightTask = new ComplexCaculateTask(middle, end);
            //执行任务
            leftTask.fork();
            rightTask.fork();
            //汇总分任务
            Integer leftResult = leftTask.join();
            Integer rightResult = rightTask.join();
            sum = leftResult + rightResult;
        }
        return sum;
    }

    public Integer computeSum() {
        Integer sum = 0;
        for (int i = start; i <= end; i++) {
            sum += i;
        }
        return sum;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ComplexCaculateTask task = new ComplexCaculateTask(1,  2);
        Future<Integer> result = forkJoinPool.submit(task);
        try {
            System.out.println("任务拆分: " + result.get());
            System.out.println("总共消耗: " + (System.currentTimeMillis() - start) + " ms.");
            System.out.println("普通计算: " + task.computeSum());
            System.out.println("总共消耗: " + (System.currentTimeMillis() - start) + " ms.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        forkJoinPool.shutdown();
    }
}