import {useState,useEffect,useRef,useCallback} from 'react';
import {api} from './api';
const cache=new Map(),inflight=new Map();let epoch=0;
export function clearDataCache(){epoch++;cache.clear();inflight.clear()}
window.addEventListener('nexus-data-invalidated',clearDataCache);
const ttl=p=>p==='/catalog'?120000:p==='/admin/graph'?20000:p==='/admin/users'?15000:p==='/reports'?12000:p==='/admin/activity'||p==='/notifications'?10000:0;
function get(path,force=false){const entry=cache.get(path);if(!force&&ttl(path)&&entry&&Date.now()-entry.at<ttl(path))return Promise.resolve(entry.value);if(inflight.has(path))return inflight.get(path);const generation=epoch;const req=api(path).then(value=>{if(generation!==epoch)throw new Error('Data changed; refreshing connection.');if(ttl(path))cache.set(path,{value,at:Date.now()});return value}).finally(()=>{if(inflight.get(path)===req)inflight.delete(path)});inflight.set(path,req);return req;}
export function useData(path,initial,interval=0){const [data,D]=useState(()=>cache.get(path)?.value??initial),[error,E]=useState(''),[loading,L]=useState(()=>!cache.has(path));const generation=useRef(0),initialRef=useRef(initial),active=useRef(false);initialRef.current=initial;
 const refresh=useCallback(async(force=false)=>{const serial=++generation.current;try{const v=await get(path,force);if(active.current&&serial===generation.current){D(v);E('')}}catch(e){if(active.current&&serial===generation.current)E(e.message)}finally{if(active.current&&serial===generation.current)L(false)}},[path]);
 useEffect(()=>{active.current=true;D(cache.get(path)?.value??initialRef.current);L(!cache.has(path));E('');refresh();const invalidate=()=>{if(document.hidden)return;refresh(true)};const visible=()=>{if(!document.hidden)refresh()};window.addEventListener('nexus-data-invalidated',invalidate);document.addEventListener('visibilitychange',visible);let timer=interval?setInterval(()=>{if(!document.hidden)refresh()},Math.max(10000,interval)):null;return()=>{active.current=false;generation.current++;window.removeEventListener('nexus-data-invalidated',invalidate);document.removeEventListener('visibilitychange',visible);if(timer)clearInterval(timer)}},[path,interval,refresh]);
 return {data,error,loading,reload:()=>refresh(true)};
}
