package br.com.samueltorga;

import com.mudra.LongRunningTask;
import com.mudra.LongRunningTask.TaskResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Collectors;

import static java.util.concurrent.StructuredTaskScope.*;

@Slf4j
public class StructuredTaskScopeOnFailureExecutor {

    public static void main(String[] args) throws Exception {
        log.info("Starting application...");

        // Create the tasks
        var dbTask = new LongRunningTask("dataTask", 3, "row1", false);
        var restTask = new LongRunningTask("restTask", 10, "json1", false);
        var extTask = new LongRunningTask("extTask", 5, "json2", true);

        Map<String, TaskResponse> result = execute(List.of(dbTask, restTask, extTask));

        result.forEach((k, v) -> log.info("{} : {}", k, v));

        TaskResponse extResponse = result.get("extTask");

        log.info("extResponse = {}", extResponse);

        log.info("Application finished.");
    }

    static Map<String, TaskResponse> execute(Collection<LongRunningTask> tasks) throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<StructuredTaskScope.Subtask<TaskResponse>> subTasks = tasks.stream().map(scope::fork).toList();

            scope.join();

            // If any subtask failed, throw an exception
            scope.throwIfFailed();

            return subTasks.stream()
                    .filter(sub -> sub.state() == Subtask.State.SUCCESS)
                    .map(Subtask::get)
                    .collect(Collectors.toMap(TaskResponse::name, r -> r));
        }
    }

}
