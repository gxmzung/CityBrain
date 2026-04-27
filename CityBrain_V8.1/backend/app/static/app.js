(function(){
  const $ = (s, root=document) => root.querySelector(s);
  const $$ = (s, root=document) => Array.from(root.querySelectorAll(s));
  const toastBox = $('[data-toast-box]');
  function toast(msg){ if(!toastBox) return; toastBox.textContent=msg; toastBox.classList.add('show'); clearTimeout(window.__toastTimer); window.__toastTimer=setTimeout(()=>toastBox.classList.remove('show'),1800); }
  const mealMap = {
    breakfast:{ko:'조식', title:'조식 한식', time:'08:00~09:00'},
    lunch:{ko:'중식', title:'중식 일품', time:'11:00~13:30'},
    dinner:{ko:'석식', title:'석식 한식', time:'17:30~19:00'}
  };
  function autoMeal(){ const h=new Date().getHours(); if(h<10) return 'breakfast'; if(h<16) return 'lunch'; return 'dinner'; }
  function setMeal(meal, manual=false){
    $$('.meal-tab').forEach(b=>b.classList.toggle('active', b.dataset.meal===meal));
    const m=mealMap[meal]||mealMap.lunch;
    $$('[data-meal-title]').forEach(e=>e.textContent=`${m.title} · 학생식당`);
    $$('[data-meal-time]').forEach(e=>e.textContent=m.time);
    const curMeta=$('[data-menu-current-meta]'); if(curMeta) curMeta.textContent=`${m.title} · ${m.time} · 학생식당`;
    if(manual) toast(`${m.ko} 시간대로 전환했습니다.`);
  }
  function openTab(name){
    $$('.v8-section').forEach(s=>s.classList.toggle('active', s.dataset.page===name));
    $$('.nav-btn').forEach(b=>b.classList.toggle('active', b.dataset.openTab===name));
    const phone=$('.v8-phone'); if(phone) phone.scrollTo({top:0, behavior:'smooth'});
  }
  $$('[data-open-tab]').forEach(btn=>btn.addEventListener('click',()=>{
    openTab(btn.dataset.openTab);
    if(btn.dataset.focusParticipation==='crowd') setTimeout(()=>$('#crowd-report')?.scrollIntoView({behavior:'smooth',block:'center'}),120);
  }));
  $$('.meal-tab').forEach(btn=>btn.addEventListener('click',()=>setMeal(btn.dataset.meal,true)));
  $$('[data-toast]').forEach(btn=>btn.addEventListener('click',()=>toast(btn.dataset.toast)));
  $$('.report-chip').forEach(btn=>btn.addEventListener('click',()=>{ $$('.report-chip').forEach(b=>b.classList.remove('selected')); btn.classList.add('selected'); toast(`혼잡도 제보가 접수되었습니다: ${btn.dataset.report}`); }));
  $$('.pref-chip').forEach(btn=>btn.addEventListener('click',()=>btn.classList.toggle('selected')));
  $$('.stars button').forEach(btn=>btn.addEventListener('click',()=>{ const n=Number(btn.dataset.star||4); $$('.stars button').forEach((b,i)=>b.textContent=i<n?'★':'☆'); $('[data-rating-score]').textContent=`${n}.0 / 5.0`; toast('학식 평가가 반영되었습니다.'); }));
  const fb=$('.feedback-input'); if(fb){ fb.addEventListener('input',()=> $('[data-feedback-count]').textContent=`${fb.value.length} / 200`); $('[data-submit-feedback]')?.addEventListener('click',()=>{toast('의견이 접수되었습니다.'); fb.value=''; $('[data-feedback-count]').textContent='0 / 200';}); }
  $$('[data-refresh]').forEach(b=>b.addEventListener('click',()=>{refreshState(true); toast('실시간 정보를 새로고침했습니다.');}));
  $$('.week-item').forEach(item=>item.addEventListener('click',()=>{ const meal=$('.meal-tab.active')?.dataset.meal||'lunch'; const menu= meal==='breakfast'?item.dataset.breakfast:meal==='dinner'?item.dataset.dinner:item.dataset.lunch; $('[data-menu-current-title]').textContent=menu; toast(`${item.querySelector('span').textContent} 메뉴를 확인합니다.`); }));
  function congestionClass(level){ if(level==='원활') return 'good'; if(level==='혼잡') return 'bad'; return 'warn'; }
  async function refreshState(animate=false){
    try{ const res=await fetch('/api/state',{cache:'no-store'}); if(!res.ok) return; const s=await res.json();
      $$('[data-state]').forEach(el=>{ const key=el.dataset.state; let v=s[key]; if(key==='capacity_label') v=`/ ${s.total_count}식`; if(v!==undefined){ el.textContent=v; if(animate){el.animate([{transform:'scale(1.08)'},{transform:'scale(1)'}],{duration:250});} } });
      $$('[data-congestion-class]').forEach(el=>{ el.classList.remove('good','warn','bad'); el.classList.add(congestionClass(s.congestion_level)); });
      $$('[data-progress]').forEach(el=>el.style.setProperty('--p', `${s.remaining_ratio||0}%`));
    }catch(e){}
  }
  if(document.body.dataset.live==='student'){ setMeal(autoMeal(),false); setInterval(()=>refreshState(false),2000); }
})();

// CityBrain V8.1 interactions
(function(){
  function toast(message){
    const el=document.getElementById('v81Toast');
    if(!el) return;
    el.textContent=message||'반영되었습니다.';
    el.classList.add('show');
    setTimeout(()=>el.classList.remove('show'),1800);
  }
  document.addEventListener('click',function(e){
    const scroll=e.target.closest('[data-scroll]');
    if(scroll){
      const target=document.getElementById(scroll.dataset.scroll);
      if(target) target.scrollIntoView({behavior:'smooth',block:'start'});
    }
    const report=e.target.closest('[data-report]');
    if(report){
      document.querySelectorAll('[data-report]').forEach(b=>b.classList.remove('selected'));
      report.classList.add('selected');
      toast(report.dataset.report+' 제보가 접수되었습니다.');
    }
    const t=e.target.closest('[data-toast]');
    if(t) toast(t.dataset.toast);
    const refresh=e.target.closest('[data-refresh]');
    if(refresh){ toast('실시간 정보를 새로고침했습니다.'); }
  });
})();
