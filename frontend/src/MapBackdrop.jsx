import React,{useEffect,useRef,useState} from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
export default function MapBackdrop({className='',interactive=false}){
 const el=useRef(null);const [state,S]=useState('loading');
 useEffect(()=>{if(!el.current)return;let alive=true,loaded=false;
 const map=L.map(el.current,{zoomControl:interactive,dragging:interactive,scrollWheelZoom:false,doubleClickZoom:interactive,boxZoom:false,keyboard:interactive,touchZoom:interactive,attributionControl:true,zoomSnap:0.5}).setView([17.5435,78.5735],16.5);
 map.attributionControl.setPrefix(false);
 const layer=L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png',{maxZoom:19,attribution:'© <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noreferrer">OpenStreetMap</a> contributors'});
 layer.on('tileload',()=>{loaded=true;if(alive)S('ready')});layer.on('tileerror',()=>{if(alive&&!loaded)S('error')});layer.addTo(map);
 const ro=new ResizeObserver(()=>map.invalidateSize({pan:false}));ro.observe(el.current);
 return()=>{alive=false;ro.disconnect();map.remove()};},[interactive]);
 return <div className={'nx6-map-backdrop '+className}><div ref={el} className="nx6-map-tiles" aria-label="OpenStreetMap view of BITS Pilani Hyderabad Campus"/>{state!=='ready'&&<div className="nx6-map-status" role="status">{state==='error'?'Map tiles unavailable':'Loading campus map…'} · <a href="https://www.openstreetmap.org/#map=16/17.5435/78.5735" target="_blank" rel="noreferrer">Open map ↗</a></div>}</div>
}
