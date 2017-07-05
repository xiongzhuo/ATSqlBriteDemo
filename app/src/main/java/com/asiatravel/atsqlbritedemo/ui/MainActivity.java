package com.asiatravel.atsqlbritedemo.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.asiatravel.atsqlbritedemo.ATSqlApplication;
import com.asiatravel.atsqlbritedemo.R;
import com.asiatravel.atsqlbritedemo.db.ATDbManager;
import com.asiatravel.atsqlbritedemo.model.Person;
import com.asiatravel.atsqlbritedemo.tools.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rlv_allperson)
    RecyclerView allPersonRecylerView;
    @Bind(R.id.et_age_input)
    EditText ageInput;
    @Bind(R.id.et_name_input)
    EditText nameInput;

    private List<Person> dataForRecylerView;
    private ATDbManager dbManager;
    private PersonAdapter personAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initEvent();
    }

    @OnClick(R.id.tv_add)
    void addPerson() {
        String personName = nameInput.getText().toString();
        String personAge = ageInput.getText().toString();
        if (TextUtils.isEmpty(personAge)) {
            Snackbar.make(fab, "请输入年龄!", Snackbar.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(personName)) {
            Snackbar.make(fab, "请输入姓名!", Snackbar.LENGTH_SHORT).show();
        } else {
            // 调用add person
            Person addPerson = new Person();
            addPerson.setAge(Integer.valueOf(personAge));
            addPerson.setName(personName);
            long state = dbManager.addPerson(addPerson);
            if (state > 0) {
                Snackbar.make(fab, "添加" + personName + "成功", Snackbar.LENGTH_SHORT).show();
            }
            // tips  没有调用查询语句,rxjava根据表数据的变化,会输出新的数据
        }
    }

    @OnClick(R.id.tv_search)
    void searchPerson() {
        final String personName = nameInput.getText().toString();
        if (TextUtils.isEmpty(personName)) {
            Snackbar.make(fab, "输入名字,查询!!", Snackbar.LENGTH_SHORT).show();
        } else {
            Observable<List<Person>> listObservable = dbManager.queryPersonByName(personName);
            listObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Person>>() {
                        @Override
                        public void onCompleted() {
                            this.unsubscribe();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(e.toString());
                        }

                        @Override
                        public void onNext(List<Person> persons) {
                            Logger.e(TAG, "查询结果: >>>>>" + persons.size() + persons.toString());
                            if (persons.size() > 0) {
                                dataForRecylerView.clear();
                                dataForRecylerView.addAll(persons);
                                personAdapter.notifyDataSetChanged();
                            } else {
                                Snackbar.make(fab, "---没有叫" + personName + "这个人---", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void initData() {
        dataForRecylerView = new ArrayList<Person>();
        personAdapter = new PersonAdapter();
        dbManager = ATSqlApplication.getApplication().getAtDbManager();
        queryPerson();
    }

    private void queryPerson() {
        Observable<List<Person>> listObservable = dbManager.queryPerson();
        listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Person>>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Person> persons) {
                        Logger.e(TAG, "onNext: >>>>>" + persons.size() + persons.toString());
                        dataForRecylerView.clear();
                        dataForRecylerView.addAll(persons);
                        personAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void initView() {
        ButterKnife.bind(this);
    }

    private void initEvent() {
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "重新加载数据", Snackbar.LENGTH_SHORT).show();
                queryPerson();
            }
        });

        allPersonRecylerView.setLayoutManager(new LinearLayoutManager(this));
        allPersonRecylerView.setAdapter(personAdapter);
        personAdapter.setRecylerClickListener(new OnRecylerClickListener() {
            @Override
            public void onReItemClick(int position) {
                Person person = dataForRecylerView.get(position);
                Snackbar.make(fab, "---点击了" + person.getName() + "---", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onReItemLongClick(int position) {
                Person person = dataForRecylerView.get(position);
                int state = dbManager.deletePersonByName(person.getName());
                if (state > 0) {
                    Snackbar.make(fab, "删除" + person.getName() + "成功", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnRecylerClickListener {
        void onReItemClick(int position);

        void onReItemLongClick(int position);
    }


    /**
     * recycleview 适配器
     */
    class PersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private OnRecylerClickListener recylerClickListener;

        public void setRecylerClickListener(OnRecylerClickListener recylerClickListener) {
            this.recylerClickListener = recylerClickListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = new PersonVH(LayoutInflater
                    .from(MainActivity.this)
                    .inflate(R.layout.item_person, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Person person = dataForRecylerView.get(position);
            if (null != person) {
                PersonVH personVH = (PersonVH) holder;
                int age = person.getAge();
                String name = person.getName();
                if (!TextUtils.isEmpty(name)) {
                    personVH.pName.setText(name);
                }
                personVH.pAge.setText(age + "岁");

                if (null != recylerClickListener) {
                    personVH.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recylerClickListener.onReItemClick(position);
                        }
                    });

                    personVH.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            recylerClickListener.onReItemLongClick(position);
                            return false;
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return dataForRecylerView.size();
        }


        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        class PersonVH extends RecyclerView.ViewHolder {
            @Bind(R.id.tv_name)
            TextView pName;
            @Bind(R.id.tv_age)
            TextView pAge;

            public PersonVH(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
