package net.thenova.socialize.util.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2019 ipr0james
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public enum TaskHandler {
    INSTANCE;

    private final ScheduledExecutorService service;
    private final Map<Long, List<Task>> currentTasks = new HashMap<>();

    TaskHandler() {
        this.service = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Schedule a delayed task to be run as a System Task.
     * System Task - Separate handling from per guild tasks, uses -1 as a guildID.
     *
     * @param runnable - Runnable to be run, used as identifier
     * @param wait - Period to wait before executing
     * @param unit - TimeUnit to be used for delay timing
     */
    public void scheduleSystemDelayed(final Runnable runnable, final long wait, final TimeUnit unit) {
        this.scheduleDelayed(-1, runnable, wait, unit);
    }

    /**
     * Schedule a delayed task for a guild.
     *
     * @param guildID - Guild scheduling the task
     * @param runnable - Runnable to be run, used as identifier
     * @param wait - Period to wait before executing
     * @param unit - TimeUnit to be used for delay timing
     */
    public void scheduleDelayed(final long guildID, final Runnable runnable, final long wait, final TimeUnit unit) {
        if(!this.currentTasks.containsKey(guildID)) {
            this.currentTasks.put(guildID, new ArrayList<>());
        }

        this.currentTasks.get(guildID).add(new Task(runnable.getClass(), this.service.schedule(runnable, wait, unit)));
    }

    /**
     * Schedule a repeating task to be run as a System Task.
     * System Task - Separate handling from per guild tasks, uses -1 as a guildID.
     *
     * @param runnable - Runnable to be run, used as identifier
     * @param wait - Period to wait before executing
     * @param unit - TimeUnit to be used for delay timing
     */
    public ScheduledFuture scheduleSystemRepeating(final Runnable runnable, final long start, final long wait, final TimeUnit unit) {
        return this.scheduleRepeating(-1, runnable, start, wait, unit);
    }

    /**
     * Schedule a repeating task for a guild.
     *
     * @param guildID - Guild scheduling the task
     * @param runnable - Runnable to be run, used as identifier
     * @param wait - Period to wait before executing
     * @param unit - TimeUnit to be used for delay timing
     */
    public ScheduledFuture scheduleRepeating(final long guildID, final Runnable runnable, final long start, final long wait, final TimeUnit unit) {
        if(!this.currentTasks.containsKey(guildID)) {
            this.currentTasks.put(guildID, new ArrayList<>());
        }

        final ScheduledFuture task = this.service.scheduleAtFixedRate(runnable, start, wait, unit);
        this.currentTasks.get(guildID).add(new Task(runnable.getClass(), task));
        return task;
    }

    /**
     * Fetch a System task from the active tasks
     * System Task - Separate handling from per guild tasks, uses -1 as a guildID.
     *
     * @param clazz - Class of the task being run
     * @return - Return the Task scheduled
     */
    public ScheduledFuture getSystemTask(final Class<? extends Runnable> clazz) {
        return this.getTask(-1L, clazz);
    }

    /**
     * Fetch a task from the active tasks for a guild
     *
     * @param guildID - Guild of the task being executed
     * @param clazz - Class of the task being run
     * @return - Return the Task scheduled
     */
    public ScheduledFuture getTask(final long guildID, final Class<? extends Runnable> clazz) {
        if(!this.currentTasks.containsKey(guildID)) {
            return null;
        }

        final Task task = this.currentTasks.get(guildID).stream()
                .filter(object -> object.getClazz().equals(clazz))
                .findFirst()
                .orElse(null);

        return task == null ? null : task.getTask();
    }
}
