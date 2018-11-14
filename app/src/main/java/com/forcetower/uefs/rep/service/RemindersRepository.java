/*
 * Copyright (c) 2018.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.forcetower.uefs.rep.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.TodoItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RemindersRepository {
    private final AppDatabase database;
    private final AppExecutors executors;
    private final Context context;

    @Inject
    public RemindersRepository(AppDatabase database, AppExecutors executors, Context context) {
        this.database = database;
        this.executors = executors;
        this.context = context;
    }

    public void prepareRemindersBackup() {
        executors.diskIO().execute(() -> {
            List<TodoItem> items = database.todoItemDao().getAllTodoItemsDirect();
            Access access = database.accessDao().getAccessDirect();
            if (access == null) return;

            String username = access.getUsernameFixed();
            executors.mainThread().execute(() -> {
                if (!items.isEmpty()) {
                    DatabaseReference reminders = FirebaseDatabase.getInstance().getReference("reminders_backup");
                    DatabaseReference path = reminders.child(username);
                    for (TodoItem item : items) {
                        DatabaseReference td = path.child("td_" + item.getUid());
                        td.child("title").setValue(item.getTitle());
                        td.child("date").setValue(item.getDate());
                        td.child("message").setValue(item.getMessage());
                        td.child("time_limit").setValue(item.isHasTimeLimit());
                        td.child("shown").setValue(item.isShown());
                        td.child("completed").setValue(item.isCompleted());
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(items);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    preferences.edit().putString("__backup_todo_items_old__", json).apply();
                }
            });
        });
    }
}
