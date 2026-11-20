import React, {useState} from 'react';
import {
    ArrowRight, ChevronRight, Plus, Network, Bell,
    Inbox, CheckCircle2, Timer, Activity, Shield
} from 'lucide-react';
import {api} from './api';
import {CampusMap,CampusActivity,LocationPicker,SmartAssist} from './campus';

const closed = i =>
    ['RESOLVED','VERIFIED','CLOSED'].includes(i.status);

const readable = s =>
    String(s || '').replaceAll('_',' ').toLowerCase();

const ticket = i =>
    'INC-' + String(i.id).padStart(4,'0');

export function Mascot({size=100}) {
    return ;
}

function TicketRows({rows,open,Pill}) {
    return <div className="v2-ticket-list">
        {rows.map(i =>
            <button key={i.id} onClick={()=>open(i.id)}>
                <div>
                    <small>{ticket(i)} · {i.service}</small>
                    <b>{i.title}</b>
                </div>
                <Pill value={i.status}/>
                <ChevronRight size={16}/>
            </button>
        )}
        {!rows.length && <div className="v2-empty">
            
            <p>Nothing to show here yet.</p>
        </div>}
    </div>;
}

export function OverviewV2({user,go,open,ui}) {
    const {
        useData,Chart,Graph,Panel,Pill,Countdown,Problem
    } = ui;

    const {data:r,error,loading} = useData(
        '/reports',{incidents:[]},15000
    );

    const [selected,select] = useState(null);

    const all = r.incidents || [];
    const current = all.filter(i=>!closed(i));
    const completed = all.filter(closed);
    const firstName = user.name.split(' ')[0];

    const focused = current.find(i=>i.id===selected)
        || current[0];

    if(user.role === 'EMPLOYEE') {
        return <div className="employee-home">

            <Problem value={error}/>

            <section className="employee-welcome">
                <div>
                    <span className="v2-kicker">YOUR IT SUPPORT SPACE</span>
                    <h1>Hey, {firstName}.</h1>
                    <h2>What can we help you fix?</h2>
                    <p>Something not working? Tell us about it.
                    We'll take care of the technical stuff.</p>

                    <button className="primary"
                        onClick={()=>go('report')}>
                        <Plus size={20}/> Report a problem
                    </button>
                </div>

                
            </section>

            <div className="employee-shortcuts">
                <button onClick={()=>go('incidents',{activeOnly:true})}>
                    <Inbox size={25}/>
                    <strong>{current.length}</strong>
                    <b>Open requests</b>
                    <small>Track your ongoing issues</small>
                    <ArrowRight size={17}/>
                </button>
                <button onClick={()=>go('incidents')}>
                    <CheckCircle2 size={25}/>
                    <strong>{completed.length}</strong>
                    <b>Resolved requests</b>
                    <small>View your support history</small>
                    <ArrowRight size={17}/>
                </button>
                <button onClick={()=>go('notifications')}>
                    <Bell size={25}/>
                    <strong>Updates</strong>
                    <b>Notifications</b>
                    <small>Hear back from your support team</small>
                    <ArrowRight size={17}/>
                </button>
            </div>

            <Panel title="Your recent requests"
                extra={<button onClick={()=>go('incidents')}>
                    View all <ArrowRight size={15}/>
                </button>}>

                <TicketRows rows={
                    [...current,...completed].slice(0,5)
                } open={open} Pill={Pill}/>

            </Panel>

            <div className="support-tip">
                
                <div>
                    <b>Not sure what the problem is?</b>
                    <p>That's okay. You don't need to be an IT expert
                    to create a support request.</p>
                </div>
                <button onClick={()=>go('report')}>Get help</button>
            </div>
        </div>;
    }

    if(user.role === 'TECHNICIAN') {
        return <div className="technician-home">

            <div className="heading">
                <div>
                    <span className="v2-kicker">ENGINEERING WORKBENCH</span>
                    <h1>Your operations queue.</h1>
                    <p>Welcome back, {firstName}.</p>
                </div>
                <button onClick={()=>go('workload')}>
                    <span className="dot"/>
                    {user.available?'Available':'Unavailable'}
                </button>
            </div>

            <Problem value={error}/>

            <div className="tech-metrics">
                <div><span>Assigned workload</span>
                    <strong>{current.length}</strong>
                    <small>Capacity: {user.capacity || '—'}</small>
                </div>
                <div><span>Critical incidents</span>
                    <strong>{r.critical ?? 0}</strong>
                    <small>Requires immediate attention</small>
                </div>
                <div><span>SLA at risk</span>
                    <strong>{r.risk ?? 0}</strong>
                    <small>Approaching target</small>
                </div>
                <div><span>Resolved</span>
                    <strong>{completed.length}</strong>
                    <small>Completed investigation</small>
                </div>
            </div>

            <div className="tech-workbench">
                <Panel title="Active queue"
                    sub="Ordered by incident priority">

                    <div className="tech-queue">
                        {current.map(i=>
                            <button key={i.id}
                                className={focused?.id===i.id?'focused':''}
                                onClick={()=>select(i.id)}>
                                <span className="tech-ticket-id">
                                    {ticket(i)}
                                    <Pill value={i.priority}/>
                                </span>
                                <b>{i.title}</b>
                                <small>{i.service}</small>
                                <Countdown i={i}/>
                            </button>
                        )}

                        {!current.length && <div className="v2-empty">
                            
                            <p>Your queue is clear.</p>
                        </div>}
                    </div>
                </Panel>

                <Panel title="Investigation workspace">
                    {focused ? <>
                        <span className="v2-kicker">{ticket(focused)}</span>
                        <h2>{focused.title}</h2>

                        <div className="pillset">
                            <Pill value={focused.priority}/>
                            <Pill value={focused.status}/>
                        </div>

                        <p className="tech-description">
                            {focused.description}
                        </p>

                        <div className="tech-sla">
                            <Timer size={22}/>
                            <div>
                                <small>Resolution deadline</small>
                                <b><Countdown i={focused}/></b>
                            </div>
                        </div>

                        <p className="muted">
                            Affected service: {focused.service}
                        </p>

                        <button className="primary wide"
                            onClick={()=>open(focused.id)}>
                            Open investigation <ArrowRight size={17}/>
                        </button>
                    </> : <div className="v2-empty">
                        
                        <p>Select an incident to begin.</p>
                    </div>}
                </Panel>
            </div>
        </div>;
    }

    const days = Array.from({length:14},(_,i)=>{
        const d = new Date();
        d.setUTCDate(d.getUTCDate()-13+i);
        return d.toISOString().slice(0,10);
    });

    const volume = days.map(d=>
        all.filter(i=>i.createdAt?.startsWith(d)).length
    );

    const resolved = days.map(d=>
        all.filter(i=>i.resolvedAt?.startsWith(d)).length
    );

    const critical = current.filter(i=>i.priority==='P1');

    const metrics = [
        ['Active incidents',r.open,'incidents',
            {activeOnly:true}],
        ['Critical incidents',r.critical,'incidents',
            {priority:'P1',activeOnly:true}],
        ['SLA at risk',r.risk,'sla',
            {sla:'WARNING'}],
        ['SLA compliance',
            r.compliance==null?'—':r.compliance+'%',
            'analytics',{}]
    ];

    return <div className="admin-home">

        <Problem value={error}/>

        <section className="admin-intro">
            <div>
                <span className="v2-kicker">
                    NEXUS / OPERATIONS INTELLIGENCE
                </span>
                <h1>Overview panel.</h1>
                <p>Your entire IT operation, in perspective.</p>
            </div>

            <div className="insight-stack">
                <div className="insight-card">
                    <span><Activity size={16}/> Operational insight</span>
                    <h3>{critical.length
                        ? `${critical.length} critical incidents need attention.`
                        : 'No active critical incidents.'}
                    </h3>
                    <button onClick={()=>go('incidents',{
                        priority:'P1',activeOnly:true
                    })}>Investigate <ArrowRight size={16}/></button>
                </div>
            </div>
        </section>

        {all.some(i=>i.title?.startsWith('[Demo]')) &&
            <p className="demo-indicator">
                <span className="dot"/>
                Workspace includes fictional demonstration records
            </p>
        }

        <div className="admin-metrics">
            {metrics.map(([name,value,page,filter],k)=>
                <button key={name}
                    onClick={()=>go(page,filter)}>
                    <span>{name}
                        <ChevronRight size={16}/>
                    </span>
                    <strong>{loading?'…':value??0}</strong>
                    <div className="metric-track">
                        <i style={{width:`${
                            k===3?(r.compliance||0):
                            Math.min(100,100*Number(value||0)
                                /Math.max(1,r.total||1))
                        }%`}}/>
                    </div>
                </button>
            )}
        </div>

        <div className="campus-command"><Panel title="Campus operations map" sub="Incident locations · selected buildings"><CampusMap incidents={all} open={open} useData={useData}/></Panel><Panel title="Operations pulse" sub="Refreshes every 12 seconds"><CampusActivity useData={useData} open={open}/></Panel></div>
        <div className="admin-main-grid">

            <div>
                <Panel title="Incident activity"
                    sub="Last 14 days · UTC">

                    <Chart height={240} option={{
                        color:['#141a16','#64db8b'],
                        grid:{left:32,right:15,top:18,bottom:40},
                        legend:{bottom:0,textStyle:{color:'#666'}},
                        xAxis:{
                            type:'category',
                            data:days.map(d=>d.slice(5))
                        },
                        yAxis:{
                            type:'value',
                            minInterval:1,
                            splitLine:{lineStyle:{color:'#e9ece9'}}
                        },
                        series:[
                            {
                                name:'Reported',
                                type:'bar',
                                barMaxWidth:13,
                                data:volume
                            },
                            {
                                name:'Resolved',
                                type:'line',
                                smooth:true,
                                data:resolved
                            }
                        ]
                    }}/>
                </Panel>

                <Panel title="Infrastructure intelligence"
                    sub="Incident-derived service health"
                    extra={<button onClick={()=>go('infrastructure')}>
                        Explore <ArrowRight size={15}/>
                    </button>}>
                    <Graph small/>
                </Panel>
            </div>

            <div>
                <Panel title="Priority watch"
                    sub="Incidents requiring attention">

                    <TicketRows rows={current.slice(0,7)}
                        open={open} Pill={Pill}/>

                    <button className="wide"
                        onClick={()=>go('incidents')}>
                        Open incident centre <ArrowRight size={15}/>
                    </button>
                </Panel>

                <Panel title="Service level performance"
                    sub={`${r.evaluatedTargets||0} evaluated SLA targets`}>

                    <Chart height={220} option={{
                        series:[{
                            
    type:'pie',
    radius:['72%','88%'],
    center:['50%','50%'],
    silent:true,
    animation:false,
    label:{show:false},
    data:[
        {
            value:Math.max(0,Math.min(100,
                Number(r.compliance)||0)),
            itemStyle:{color:'#63dc88'}
        },
        {
            value:100-Math.max(0,Math.min(100,
                Number(r.compliance)||0)),
            itemStyle:{color:'#e7ebe7'}
        }
    ]

                        }]
                    }}/>

                    <button className="wide"
                        onClick={()=>go('analytics')}>
                        Explore analytics <ArrowRight size={15}/>
                    </button>
                </Panel>
            </div>
        </div>
    </div>;
}

const categoryNames = {
    HARDWARE:'Device or equipment',
    SOFTWARE:'Software problem',
    NETWORK:'Internet or connectivity',
    DATABASE:'Data or database',
    CLOUD:'Cloud service',
    SECURITY:'Security concern',
    AUTHENTICATION:'Login or password',
    APPLICATION:'Application not working',
    STORAGE:'Files or storage'
};

export function ReportV2({open,ui}) {

    const {useData,Pill,Problem} = ui;

    const {data:c,error,loading,reload} =
        useData('/catalog',{services:[],categories:[]});

    const [step,S] = useState(0);
    const [busy,B] = useState(false);
    const [err,E] = useState('');
    const [preview,V] = useState(null);

    const [f,F] = useState({
        serviceId:'',
        categoryId:'',
        locationCode:'',locationNote:'',
        title:'',
        description:'',
        impact:1,
        urgency:1
    });

    const put = (k,v)=>F(x=>({...x,[k]:v}));

    const payload = ()=>({
        ...f,
        serviceId:Number(f.serviceId),
        categoryId:Number(f.categoryId)
    });

    async function next(){
        E('');

        if(step<2){
            S(step+1);
            return;
        }

        B(true);

        try {
            if(step===2){
                V(await api(
                    '/incidents/preview','POST',payload()
                ));
                S(3);
            }else{
                const incident = await api(
                    '/incidents','POST',payload()
                );

                open(incident.id);
            }
        }catch(e){
            E(e.message);
        }finally{
            B(false);
        }
    }

    const valid = [
        Boolean(f.serviceId),
        Boolean(f.categoryId && f.title.trim()
            && f.description.trim()),
        true,
        Boolean(preview)
    ][step];

    return <div className="report-v2">

        <div className="heading">
            <div>
                <span className="v2-kicker">LET'S GET IT FIXED</span>
                <h1>Report a problem.</h1>
                <p>Four simple steps. We'll handle the rest.</p>
            </div>
        </div>

        <div className="report-layout">

            <section className="report-card">

                <div className="report-progress">
                    {['Service','Problem','Impact','Review'].map((s,i)=>
                        <div key={s}
                            className={step===i?'on':step>i?'done':''}>
                            <span>{step>i?'✓':i+1}</span>
                            {s}
                        </div>
                    )}
                </div>

                <Problem value={error||err}/>

                {!loading && (!c.categories.length||!c.services.length)
                    && <div className="catalog-warning">
                        <p>The service catalogue is unavailable.</p>
                        <button onClick={reload}>Reload catalogue</button>
                    </div>
                }

                <div className="report-form">

                    {step===0 && <>
                        <h2>What isn't working?</h2><LocationPicker sites={c.locations||[]} value={f.locationCode} note={f.locationNote} put={put}/>
                        <p>Choose the service you're having trouble with.</p>

                        <div className="report-services">
                            {c.services.map(s=>
                                <button key={s.id}
                                    className={f.serviceId===s.id?'chosen':''}
                                    onClick={()=>put('serviceId',s.id)}>
                                    <Network size={23}/>
                                    <b>{s.name}</b>
                                    <ChevronRight size={16}/>
                                </button>
                            )}
                        </div>
                    </>}

                    {step===1 && <>
                        <h2>Tell us what happened.</h2><SmartAssist categories={c.categories} apply={(s)=>F(old=>({...old,categoryId:c.categories.find(x=>x.name===s.category)?.id||old.categoryId,description:s.description}))}/>

                        <label>What kind of problem is it?</label>

                        <div className="category-choices">
                            {c.categories.map(x=>
                                <button key={x.id}
                                    className={f.categoryId===x.id?'chosen':''}
                                    onClick={()=>put('categoryId',x.id)}>
                                    {categoryNames[x.name]||readable(x.name)}
                                </button>
                            )}
                        </div>

                        <label>Give it a short title
                            <input value={f.title} maxLength={180}
                                placeholder="I can't access my email"
                                onChange={e=>put('title',e.target.value)}/>
                        </label>

                        <label>Describe what happened
                            <textarea rows={5}
                                value={f.description}
                                maxLength={8000}
                                placeholder="What were you trying to do?"
                                onChange={e=>put(
                                    'description',e.target.value
                                )}/>
                        </label>
                    </>}

                    {step===2 && <>
                        <h2>How much is this affecting you?</h2>

                        {[
                            ['impact','Who is affected?',
                                ['Just me','My team','Everyone']],
                            ['urgency','Can you continue working?',
                                ['Yes, it can wait',
                                 'Partially',
                                 'No, I am blocked']]
                        ].map(([k,title,options])=>
                            <div key={k} className="impact-group">
                                <h3>{title}</h3>

                                {options.map((o,i)=>
                                    <button key={o}
                                        className={f[k]===i+1?'chosen':''}
                                        onClick={()=>put(k,i+1)}>
                                        <span className="choice-radio"/>
                                        {o}
                                    </button>
                                )}
                            </div>
                        )}
                    </>}

                    {step===3 && preview && <>
                        <h2>Ready to send.</h2>

                        <div className="review-summary">
                            <Pill value={preview.priority}/>
                            <h3>{f.title}</h3>
                            <p>{f.description}</p>
                            <hr/>
                            <p>Location: {(c.locations||[]).find(x=>x.code===f.locationCode)?.name||"Remote / not specified"} {f.locationNote}</p>
                            <p>Service: {
                                c.services.find(s=>s.id===f.serviceId)?.name
                            }</p>
                            <p>Response target: {
                                preview.target.responseMinutes
                            } minutes</p>
                            <p>Resolution target: {
                                preview.target.resolutionMinutes
                            } minutes</p>
                            <small>Priority calculated by the NEXUS engine.</small>
                        </div>

                        {preview.duplicates?.length>0 &&
                            <div className="callout">
                                <b>Possibly related requests</b>
                                {preview.duplicates.map(d=>
                                    <span key={d.id}>{d.title} · {
                                        Math.round(d.similarity*100)
                                    }% similarity</span>
                                )}
                            </div>
                        }
                    </>}

                </div>

                <footer className="report-footer">
                    <button disabled={step===0||busy}
                        onClick={()=>S(step-1)}>Back</button>
                    <button className="primary"
                        disabled={!valid||busy||loading}
                        onClick={next}>
                        {busy?'Working…':step===3?'Submit request':'Continue'}
                        <ArrowRight size={17}/>
                    </button>
                </footer>
            </section>

            <aside className="report-companion">
                
                <h3>I'm here to help.</h3>
                <p>{
                    [
                        "Let's start by finding the affected service.",
                        "A little detail helps us find the right technician.",
                        "This helps NEXUS calculate the correct priority.",
                        "Your request will be automatically routed to support."
                    ][step]
                }</p>
            </aside>
        </div>
    </div>;
}
