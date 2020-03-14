package project.projectfive.gpsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

import project.projectfive.gpsapp.db.LocationChain
import project.projectfive.gpsapp.db.LocationData

class MainActivity : AppCompatActivity() {
    lateinit var gpsViewModel:GpsViewModel
    lateinit var  gpsChainViewModel:GpsChainViewModel
    lateinit var nv:NavigationView
    lateinit var dl:DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gpsViewModel = ViewModelProvider(this).get(GpsViewModel::class.java)
        gpsChainViewModel = ViewModelProvider(this).get(GpsChainViewModel::class.java)
        var fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(fragment == null){
            fragment = GpsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container,fragment)
                .commit()
        }
        nv = findViewById(R.id.navigation)
        dl = findViewById(R.id.dl)

        val chainsObserver = Observer<List<LocationChain>>{data ->
            Log.d("CHAINS","" + data.size)
            nv.menu.clear()
            for(d in data){
                nv.menu.add("${d.name}")

                nv.menu.get(nv.menu.size()-1).setOnMenuItemClickListener {
                    Log.d("ITEM", d.name)
                    supportActionBar?.title = d.name
                    gpsChainViewModel.setCurChain(d,gpsViewModel)
                    dl.closeDrawer(Gravity.LEFT)
                    true
                }
            }
        }

        gpsChainViewModel.getChainsLiveData().observe(this, chainsObserver)
        supportActionBar?.title = gpsChainViewModel.getCurrentChainName()
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_dehaze_black_18dp);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save ->{

                val builder = MaterialAlertDialogBuilder(this)
                val dialogLayout = layoutInflater.inflate(R.layout.add_dialog, null)
                builder.setView(dialogLayout)
                val alert = builder.create()



                val ch1 = dialogLayout.findViewById<MaterialCheckBox>(R.id.ch1)
                val ch2 = dialogLayout.findViewById<MaterialCheckBox>(R.id.ch2)
                val locName = dialogLayout.findViewById<EditText>(R.id.name)
                val s = dialogLayout.findViewById<MaterialButton>(R.id.save)
                val rv = dialogLayout.findViewById<RecyclerView>(R.id.rv)
                val adapter = RecViewAdapter()
                val sc = dialogLayout.findViewById<NestedScrollView>(R.id.sc)

                ch1.setOnClickListener {
                    ch2.isChecked = !ch1.isChecked
                    if(ch2.isChecked)
                        s.text = getString(R.string.oks2)
                    else
                        s.text = getString(R.string.oks1)
                    locName.isEnabled = ch1.isChecked
                    adapter.isEnabled = ch2.isChecked
                    sc.isEnabled = ch2.isChecked
                    adapter.notifyDataSetChanged()
                }
                ch2.setOnClickListener {
                    ch1.isChecked = !ch2.isChecked
                    if(ch2.isChecked)
                        s.text = getString(R.string.oks2)
                    else
                        s.text = getString(R.string.oks1)
                    locName.isEnabled = ch1.isChecked
                    adapter.isEnabled = ch2.isChecked
                    sc.isEnabled = ch2.isChecked
                    adapter.notifyDataSetChanged()
                }
                val save = dialogLayout.findViewById<MaterialButton>(R.id.save)
                save.setOnClickListener {
                    gpsViewModel.saveChainPoints(locName.text.toString())
                    alert.dismiss()
                }
                val cancel = dialogLayout.findViewById<MaterialButton>(R.id.cancel)
                cancel.setOnClickListener {
                    alert.dismiss()
                }

                val lm = LinearLayoutManager(this)
                lm.orientation = LinearLayoutManager.VERTICAL
                rv.layoutManager = lm
                rv.adapter = adapter
                adapter.data = gpsChainViewModel.chains.value as List<LocationChain>

                val p = sc.layoutParams
                val scale = this.getResources().getDisplayMetrics().density;
                var dps = 56
                if(adapter.itemCount < 4){
                    dps *= adapter.itemCount
                } else {
                    dps *= 3
                }
                p.height = (dps * scale + 0.5f).toInt();
                sc.layoutParams = p
                if(adapter.itemCount == 0){
                    ch1.isEnabled = false
                    ch2.isEnabled = false
                }

                locName.setText(getString(R.string.new_loc) + " " + gpsChainViewModel.getChainNewNameNumber(),TextView.BufferType.EDITABLE)
                alert.show()
            }
            R.id.link ->{

            }
            R.id.delete -> {
                val s = gpsChainViewModel.getCurrentChainName()
                if(!s.equals("")) {
                    val builder = MaterialAlertDialogBuilder(this)
                    val dialogLayout = layoutInflater.inflate(R.layout.delete_dialog, null)
                    builder.setView(dialogLayout)
                    val alert = builder.create()
                    alert.show()
                    val t = dialogLayout.findViewById<TextView>(R.id.name)
                    t.text = s + " ?"

                    val ok = dialogLayout.findViewById<MaterialButton>(R.id.ok)
                    ok.setOnClickListener {
                        supportActionBar?.title = gpsChainViewModel.getNextChainName()
                        gpsChainViewModel.deleteCurrentChain(gpsViewModel, supportActionBar)
                        alert.dismiss()
                    }
                    val cancel = dialogLayout.findViewById<MaterialButton>(R.id.cancel)
                    cancel.setOnClickListener{
                        alert.dismiss()
                    }
                }

            }
            else -> {
                if(dl.isDrawerOpen(Gravity.LEFT))
                    dl.closeDrawer(Gravity.LEFT)
                else
                    dl.openDrawer(Gravity.LEFT)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    class RecViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        lateinit var data:List<LocationChain>
        var isEnabled = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val item = LayoutInflater.from(parent.context).inflate(R.layout.rv_element, parent, false)
            return RecViewHolder(item)
        }

        override fun getItemCount(): Int {
            return data.size;
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as RecViewHolder).bind(data.get(position).name,isEnabled)
        }


    }
    class RecViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val button = itemView.findViewById<MaterialButton>(R.id.button2)

        init {
            button.isEnabled = false
        }

        fun bind(name:String, isEnable:Boolean){
            button.text = name
            button.isEnabled = isEnable
        }
    }

}
