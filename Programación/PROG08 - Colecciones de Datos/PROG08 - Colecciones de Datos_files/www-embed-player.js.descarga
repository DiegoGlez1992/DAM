(function(){/*

 Copyright The Closure Library Authors.
 SPDX-License-Identifier: Apache-2.0
*/
'use strict';var l;function aa(a){var b=0;return function(){return b<a.length?{done:!1,value:a[b++]}:{done:!0}}}
var ba="function"==typeof Object.defineProperties?Object.defineProperty:function(a,b,c){if(a==Array.prototype||a==Object.prototype)return a;a[b]=c.value;return a};
function ca(a){a=["object"==typeof globalThis&&globalThis,a,"object"==typeof window&&window,"object"==typeof self&&self,"object"==typeof global&&global];for(var b=0;b<a.length;++b){var c=a[b];if(c&&c.Math==Math)return c}throw Error("Cannot find global object");}
var da=ca(this);function n(a,b){if(b)a:{var c=da;a=a.split(".");for(var d=0;d<a.length-1;d++){var e=a[d];if(!(e in c))break a;c=c[e]}a=a[a.length-1];d=c[a];b=b(d);b!=d&&null!=b&&ba(c,a,{configurable:!0,writable:!0,value:b})}}
n("Symbol",function(a){function b(f){if(this instanceof b)throw new TypeError("Symbol is not a constructor");return new c(d+(f||"")+"_"+e++,f)}
function c(f,g){this.i=f;ba(this,"description",{configurable:!0,writable:!0,value:g})}
if(a)return a;c.prototype.toString=function(){return this.i};
var d="jscomp_symbol_"+(1E9*Math.random()>>>0)+"_",e=0;return b});
n("Symbol.iterator",function(a){if(a)return a;a=Symbol("Symbol.iterator");for(var b="Array Int8Array Uint8Array Uint8ClampedArray Int16Array Uint16Array Int32Array Uint32Array Float32Array Float64Array".split(" "),c=0;c<b.length;c++){var d=da[b[c]];"function"===typeof d&&"function"!=typeof d.prototype[a]&&ba(d.prototype,a,{configurable:!0,writable:!0,value:function(){return ea(aa(this))}})}return a});
function ea(a){a={next:a};a[Symbol.iterator]=function(){return this};
return a}
function q(a){var b="undefined"!=typeof Symbol&&Symbol.iterator&&a[Symbol.iterator];return b?b.call(a):{next:aa(a)}}
function fa(a){if(!(a instanceof Array)){a=q(a);for(var b,c=[];!(b=a.next()).done;)c.push(b.value);a=c}return a}
function ha(a,b){return Object.prototype.hasOwnProperty.call(a,b)}
var ia="function"==typeof Object.assign?Object.assign:function(a,b){for(var c=1;c<arguments.length;c++){var d=arguments[c];if(d)for(var e in d)ha(d,e)&&(a[e]=d[e])}return a};
n("Object.assign",function(a){return a||ia});
var ka="function"==typeof Object.create?Object.create:function(a){function b(){}
b.prototype=a;return new b},la=function(){function a(){function c(){}
new c;Reflect.construct(c,[],function(){});
return new c instanceof c}
if("undefined"!=typeof Reflect&&Reflect.construct){if(a())return Reflect.construct;var b=Reflect.construct;return function(c,d,e){c=b(c,d);e&&Reflect.setPrototypeOf(c,e.prototype);return c}}return function(c,d,e){void 0===e&&(e=c);
e=ka(e.prototype||Object.prototype);return Function.prototype.apply.call(c,e,d)||e}}(),ma;
if("function"==typeof Object.setPrototypeOf)ma=Object.setPrototypeOf;else{var na;a:{var oa={a:!0},pa={};try{pa.__proto__=oa;na=pa.a;break a}catch(a){}na=!1}ma=na?function(a,b){a.__proto__=b;if(a.__proto__!==b)throw new TypeError(a+" is not extensible");return a}:null}var qa=ma;
function r(a,b){a.prototype=ka(b.prototype);a.prototype.constructor=a;if(qa)qa(a,b);else for(var c in b)if("prototype"!=c)if(Object.defineProperties){var d=Object.getOwnPropertyDescriptor(b,c);d&&Object.defineProperty(a,c,d)}else a[c]=b[c];a.Y=b.prototype}
function ra(){this.D=!1;this.m=null;this.j=void 0;this.i=1;this.s=this.o=0;this.A=this.l=null}
function sa(a){if(a.D)throw new TypeError("Generator is already running");a.D=!0}
ra.prototype.v=function(a){this.j=a};
function ta(a,b){a.l={sb:b,xb:!0};a.i=a.o||a.s}
ra.prototype.return=function(a){this.l={return:a};this.i=this.s};
function t(a,b,c){a.i=c;return{value:b}}
ra.prototype.u=function(a){this.i=a};
function wa(a,b,c){a.o=b;void 0!=c&&(a.s=c)}
function xa(a,b){a.i=b;a.o=0}
function ya(a){a.o=0;var b=a.l.sb;a.l=null;return b}
function za(a){a.A=[a.l];a.o=0;a.s=0}
function Aa(a){var b=a.A.splice(0)[0];(b=a.l=a.l||b)?b.xb?a.i=a.o||a.s:void 0!=b.u&&a.s<b.u?(a.i=b.u,a.l=null):a.i=a.s:a.i=0}
function Da(a){this.i=new ra;this.j=a}
function Ea(a,b){sa(a.i);var c=a.i.m;if(c)return Fa(a,"return"in c?c["return"]:function(d){return{value:d,done:!0}},b,a.i.return);
a.i.return(b);return Ga(a)}
function Fa(a,b,c,d){try{var e=b.call(a.i.m,c);if(!(e instanceof Object))throw new TypeError("Iterator result "+e+" is not an object");if(!e.done)return a.i.D=!1,e;var f=e.value}catch(g){return a.i.m=null,ta(a.i,g),Ga(a)}a.i.m=null;d.call(a.i,f);return Ga(a)}
function Ga(a){for(;a.i.i;)try{var b=a.j(a.i);if(b)return a.i.D=!1,{value:b.value,done:!1}}catch(c){a.i.j=void 0,ta(a.i,c)}a.i.D=!1;if(a.i.l){b=a.i.l;a.i.l=null;if(b.xb)throw b.sb;return{value:b.return,done:!0}}return{value:void 0,done:!0}}
function Ha(a){this.next=function(b){sa(a.i);a.i.m?b=Fa(a,a.i.m.next,b,a.i.v):(a.i.v(b),b=Ga(a));return b};
this.throw=function(b){sa(a.i);a.i.m?b=Fa(a,a.i.m["throw"],b,a.i.v):(ta(a.i,b),b=Ga(a));return b};
this.return=function(b){return Ea(a,b)};
this[Symbol.iterator]=function(){return this}}
function Ia(a){function b(d){return a.next(d)}
function c(d){return a.throw(d)}
return new Promise(function(d,e){function f(g){g.done?d(g.value):Promise.resolve(g.value).then(b,c).then(f,e)}
f(a.next())})}
function w(a){return Ia(new Ha(new Da(a)))}
function Ja(){for(var a=Number(this),b=[],c=a;c<arguments.length;c++)b[c-a]=arguments[c];return b}
n("Reflect",function(a){return a?a:{}});
n("Reflect.construct",function(){return la});
n("Reflect.setPrototypeOf",function(a){return a?a:qa?function(b,c){try{return qa(b,c),!0}catch(d){return!1}}:null});
n("Promise",function(a){function b(g){this.i=0;this.l=void 0;this.j=[];this.D=!1;var h=this.m();try{g(h.resolve,h.reject)}catch(k){h.reject(k)}}
function c(){this.i=null}
function d(g){return g instanceof b?g:new b(function(h){h(g)})}
if(a)return a;c.prototype.j=function(g){if(null==this.i){this.i=[];var h=this;this.l(function(){h.s()})}this.i.push(g)};
var e=da.setTimeout;c.prototype.l=function(g){e(g,0)};
c.prototype.s=function(){for(;this.i&&this.i.length;){var g=this.i;this.i=[];for(var h=0;h<g.length;++h){var k=g[h];g[h]=null;try{k()}catch(m){this.m(m)}}}this.i=null};
c.prototype.m=function(g){this.l(function(){throw g;})};
b.prototype.m=function(){function g(m){return function(p){k||(k=!0,m.call(h,p))}}
var h=this,k=!1;return{resolve:g(this.R),reject:g(this.s)}};
b.prototype.R=function(g){if(g===this)this.s(new TypeError("A Promise cannot resolve to itself"));else if(g instanceof b)this.da(g);else{a:switch(typeof g){case "object":var h=null!=g;break a;case "function":h=!0;break a;default:h=!1}h?this.N(g):this.o(g)}};
b.prototype.N=function(g){var h=void 0;try{h=g.then}catch(k){this.s(k);return}"function"==typeof h?this.la(h,g):this.o(g)};
b.prototype.s=function(g){this.v(2,g)};
b.prototype.o=function(g){this.v(1,g)};
b.prototype.v=function(g,h){if(0!=this.i)throw Error("Cannot settle("+g+", "+h+"): Promise already settled in state"+this.i);this.i=g;this.l=h;2===this.i&&this.W();this.A()};
b.prototype.W=function(){var g=this;e(function(){if(g.L()){var h=da.console;"undefined"!==typeof h&&h.error(g.l)}},1)};
b.prototype.L=function(){if(this.D)return!1;var g=da.CustomEvent,h=da.Event,k=da.dispatchEvent;if("undefined"===typeof k)return!0;"function"===typeof g?g=new g("unhandledrejection",{cancelable:!0}):"function"===typeof h?g=new h("unhandledrejection",{cancelable:!0}):(g=da.document.createEvent("CustomEvent"),g.initCustomEvent("unhandledrejection",!1,!0,g));g.promise=this;g.reason=this.l;return k(g)};
b.prototype.A=function(){if(null!=this.j){for(var g=0;g<this.j.length;++g)f.j(this.j[g]);this.j=null}};
var f=new c;b.prototype.da=function(g){var h=this.m();g.Ha(h.resolve,h.reject)};
b.prototype.la=function(g,h){var k=this.m();try{g.call(h,k.resolve,k.reject)}catch(m){k.reject(m)}};
b.prototype.then=function(g,h){function k(x,v){return"function"==typeof x?function(E){try{m(x(E))}catch(G){p(G)}}:v}
var m,p,u=new b(function(x,v){m=x;p=v});
this.Ha(k(g,m),k(h,p));return u};
b.prototype.catch=function(g){return this.then(void 0,g)};
b.prototype.Ha=function(g,h){function k(){switch(m.i){case 1:g(m.l);break;case 2:h(m.l);break;default:throw Error("Unexpected state: "+m.i);}}
var m=this;null==this.j?f.j(k):this.j.push(k);this.D=!0};
b.resolve=d;b.reject=function(g){return new b(function(h,k){k(g)})};
b.race=function(g){return new b(function(h,k){for(var m=q(g),p=m.next();!p.done;p=m.next())d(p.value).Ha(h,k)})};
b.all=function(g){var h=q(g),k=h.next();return k.done?d([]):new b(function(m,p){function u(E){return function(G){x[E]=G;v--;0==v&&m(x)}}
var x=[],v=0;do x.push(void 0),v++,d(k.value).Ha(u(x.length-1),p),k=h.next();while(!k.done)})};
return b});
n("WeakMap",function(a){function b(k){this.i=(h+=Math.random()+1).toString();if(k){k=q(k);for(var m;!(m=k.next()).done;)m=m.value,this.set(m[0],m[1])}}
function c(){}
function d(k){var m=typeof k;return"object"===m&&null!==k||"function"===m}
function e(k){if(!ha(k,g)){var m=new c;ba(k,g,{value:m})}}
function f(k){var m=Object[k];m&&(Object[k]=function(p){if(p instanceof c)return p;Object.isExtensible(p)&&e(p);return m(p)})}
if(function(){if(!a||!Object.seal)return!1;try{var k=Object.seal({}),m=Object.seal({}),p=new a([[k,2],[m,3]]);if(2!=p.get(k)||3!=p.get(m))return!1;p.delete(k);p.set(m,4);return!p.has(k)&&4==p.get(m)}catch(u){return!1}}())return a;
var g="$jscomp_hidden_"+Math.random();f("freeze");f("preventExtensions");f("seal");var h=0;b.prototype.set=function(k,m){if(!d(k))throw Error("Invalid WeakMap key");e(k);if(!ha(k,g))throw Error("WeakMap key fail: "+k);k[g][this.i]=m;return this};
b.prototype.get=function(k){return d(k)&&ha(k,g)?k[g][this.i]:void 0};
b.prototype.has=function(k){return d(k)&&ha(k,g)&&ha(k[g],this.i)};
b.prototype.delete=function(k){return d(k)&&ha(k,g)&&ha(k[g],this.i)?delete k[g][this.i]:!1};
return b});
n("Map",function(a){function b(){var h={};return h.previous=h.next=h.head=h}
function c(h,k){var m=h.i;return ea(function(){if(m){for(;m.head!=h.i;)m=m.previous;for(;m.next!=m.head;)return m=m.next,{done:!1,value:k(m)};m=null}return{done:!0,value:void 0}})}
function d(h,k){var m=k&&typeof k;"object"==m||"function"==m?f.has(k)?m=f.get(k):(m=""+ ++g,f.set(k,m)):m="p_"+k;var p=h.data_[m];if(p&&ha(h.data_,m))for(h=0;h<p.length;h++){var u=p[h];if(k!==k&&u.key!==u.key||k===u.key)return{id:m,list:p,index:h,entry:u}}return{id:m,list:p,index:-1,entry:void 0}}
function e(h){this.data_={};this.i=b();this.size=0;if(h){h=q(h);for(var k;!(k=h.next()).done;)k=k.value,this.set(k[0],k[1])}}
if(function(){if(!a||"function"!=typeof a||!a.prototype.entries||"function"!=typeof Object.seal)return!1;try{var h=Object.seal({x:4}),k=new a(q([[h,"s"]]));if("s"!=k.get(h)||1!=k.size||k.get({x:4})||k.set({x:4},"t")!=k||2!=k.size)return!1;var m=k.entries(),p=m.next();if(p.done||p.value[0]!=h||"s"!=p.value[1])return!1;p=m.next();return p.done||4!=p.value[0].x||"t"!=p.value[1]||!m.next().done?!1:!0}catch(u){return!1}}())return a;
var f=new WeakMap;e.prototype.set=function(h,k){h=0===h?0:h;var m=d(this,h);m.list||(m.list=this.data_[m.id]=[]);m.entry?m.entry.value=k:(m.entry={next:this.i,previous:this.i.previous,head:this.i,key:h,value:k},m.list.push(m.entry),this.i.previous.next=m.entry,this.i.previous=m.entry,this.size++);return this};
e.prototype.delete=function(h){h=d(this,h);return h.entry&&h.list?(h.list.splice(h.index,1),h.list.length||delete this.data_[h.id],h.entry.previous.next=h.entry.next,h.entry.next.previous=h.entry.previous,h.entry.head=null,this.size--,!0):!1};
e.prototype.clear=function(){this.data_={};this.i=this.i.previous=b();this.size=0};
e.prototype.has=function(h){return!!d(this,h).entry};
e.prototype.get=function(h){return(h=d(this,h).entry)&&h.value};
e.prototype.entries=function(){return c(this,function(h){return[h.key,h.value]})};
e.prototype.keys=function(){return c(this,function(h){return h.key})};
e.prototype.values=function(){return c(this,function(h){return h.value})};
e.prototype.forEach=function(h,k){for(var m=this.entries(),p;!(p=m.next()).done;)p=p.value,h.call(k,p[1],p[0],this)};
e.prototype[Symbol.iterator]=e.prototype.entries;var g=0;return e});
function Ka(a,b,c){if(null==a)throw new TypeError("The 'this' value for String.prototype."+c+" must not be null or undefined");if(b instanceof RegExp)throw new TypeError("First argument to String.prototype."+c+" must not be a regular expression");return a+""}
n("String.prototype.endsWith",function(a){return a?a:function(b,c){var d=Ka(this,b,"endsWith");b+="";void 0===c&&(c=d.length);c=Math.max(0,Math.min(c|0,d.length));for(var e=b.length;0<e&&0<c;)if(d[--c]!=b[--e])return!1;return 0>=e}});
n("Array.prototype.find",function(a){return a?a:function(b,c){a:{var d=this;d instanceof String&&(d=String(d));for(var e=d.length,f=0;f<e;f++){var g=d[f];if(b.call(c,g,f,d)){b=g;break a}}b=void 0}return b}});
n("String.prototype.startsWith",function(a){return a?a:function(b,c){var d=Ka(this,b,"startsWith");b+="";var e=d.length,f=b.length;c=Math.max(0,Math.min(c|0,d.length));for(var g=0;g<f&&c<e;)if(d[c++]!=b[g++])return!1;return g>=f}});
n("Number.isFinite",function(a){return a?a:function(b){return"number"!==typeof b?!1:!isNaN(b)&&Infinity!==b&&-Infinity!==b}});
n("Number.isInteger",function(a){return a?a:function(b){return Number.isFinite(b)?b===Math.floor(b):!1}});
n("Number.MAX_SAFE_INTEGER",function(){return 9007199254740991});
function La(a,b){a instanceof String&&(a+="");var c=0,d=!1,e={next:function(){if(!d&&c<a.length){var f=c++;return{value:b(f,a[f]),done:!1}}d=!0;return{done:!0,value:void 0}}};
e[Symbol.iterator]=function(){return e};
return e}
n("Array.prototype.entries",function(a){return a?a:function(){return La(this,function(b,c){return[b,c]})}});
n("Number.isNaN",function(a){return a?a:function(b){return"number"===typeof b&&isNaN(b)}});
n("Object.setPrototypeOf",function(a){return a||qa});
n("Set",function(a){function b(c){this.i=new Map;if(c){c=q(c);for(var d;!(d=c.next()).done;)this.add(d.value)}this.size=this.i.size}
if(function(){if(!a||"function"!=typeof a||!a.prototype.entries||"function"!=typeof Object.seal)return!1;try{var c=Object.seal({x:4}),d=new a(q([c]));if(!d.has(c)||1!=d.size||d.add(c)!=d||1!=d.size||d.add({x:4})!=d||2!=d.size)return!1;var e=d.entries(),f=e.next();if(f.done||f.value[0]!=c||f.value[1]!=c)return!1;f=e.next();return f.done||f.value[0]==c||4!=f.value[0].x||f.value[1]!=f.value[0]?!1:e.next().done}catch(g){return!1}}())return a;
b.prototype.add=function(c){c=0===c?0:c;this.i.set(c,c);this.size=this.i.size;return this};
b.prototype.delete=function(c){c=this.i.delete(c);this.size=this.i.size;return c};
b.prototype.clear=function(){this.i.clear();this.size=0};
b.prototype.has=function(c){return this.i.has(c)};
b.prototype.entries=function(){return this.i.entries()};
b.prototype.values=function(){return this.i.values()};
b.prototype.keys=b.prototype.values;b.prototype[Symbol.iterator]=b.prototype.values;b.prototype.forEach=function(c,d){var e=this;this.i.forEach(function(f){return c.call(d,f,f,e)})};
return b});
n("Object.entries",function(a){return a?a:function(b){var c=[],d;for(d in b)ha(b,d)&&c.push([d,b[d]]);return c}});
n("Array.prototype.keys",function(a){return a?a:function(){return La(this,function(b){return b})}});
n("Array.prototype.values",function(a){return a?a:function(){return La(this,function(b,c){return c})}});
n("Array.from",function(a){return a?a:function(b,c,d){c=null!=c?c:function(h){return h};
var e=[],f="undefined"!=typeof Symbol&&Symbol.iterator&&b[Symbol.iterator];if("function"==typeof f){b=f.call(b);for(var g=0;!(f=b.next()).done;)e.push(c.call(d,f.value,g++))}else for(f=b.length,g=0;g<f;g++)e.push(c.call(d,b[g],g));return e}});
n("Object.is",function(a){return a?a:function(b,c){return b===c?0!==b||1/b===1/c:b!==b&&c!==c}});
n("Array.prototype.includes",function(a){return a?a:function(b,c){var d=this;d instanceof String&&(d=String(d));var e=d.length;c=c||0;for(0>c&&(c=Math.max(c+e,0));c<e;c++){var f=d[c];if(f===b||Object.is(f,b))return!0}return!1}});
n("String.prototype.includes",function(a){return a?a:function(b,c){return-1!==Ka(this,b,"includes").indexOf(b,c||0)}});
n("Object.values",function(a){return a?a:function(b){var c=[],d;for(d in b)ha(b,d)&&c.push(b[d]);return c}});
var y=this||self;function z(a,b,c){a=a.split(".");c=c||y;a[0]in c||"undefined"==typeof c.execScript||c.execScript("var "+a[0]);for(var d;a.length&&(d=a.shift());)a.length||void 0===b?c[d]&&c[d]!==Object.prototype[d]?c=c[d]:c=c[d]={}:c[d]=b}
function A(a,b){a=a.split(".");b=b||y;for(var c=0;c<a.length;c++)if(b=b[a[c]],null==b)return null;return b}
function Ma(a){a.ab=void 0;a.getInstance=function(){return a.ab?a.ab:a.ab=new a}}
function Oa(a){var b=typeof a;return"object"!=b?b:a?Array.isArray(a)?"array":b:"null"}
function Pa(a){var b=Oa(a);return"array"==b||"object"==b&&"number"==typeof a.length}
function Qa(a){var b=typeof a;return"object"==b&&null!=a||"function"==b}
function Ra(a){return Object.prototype.hasOwnProperty.call(a,Sa)&&a[Sa]||(a[Sa]=++Ta)}
var Sa="closure_uid_"+(1E9*Math.random()>>>0),Ta=0;function Ua(a,b,c){return a.call.apply(a.bind,arguments)}
function Va(a,b,c){if(!a)throw Error();if(2<arguments.length){var d=Array.prototype.slice.call(arguments,2);return function(){var e=Array.prototype.slice.call(arguments);Array.prototype.unshift.apply(e,d);return a.apply(b,e)}}return function(){return a.apply(b,arguments)}}
function Wa(a,b,c){Function.prototype.bind&&-1!=Function.prototype.bind.toString().indexOf("native code")?Wa=Ua:Wa=Va;return Wa.apply(null,arguments)}
function Xa(a,b){var c=Array.prototype.slice.call(arguments,1);return function(){var d=c.slice();d.push.apply(d,arguments);return a.apply(this,d)}}
function Ya(a,b){function c(){}
c.prototype=b.prototype;a.Y=b.prototype;a.prototype=new c;a.prototype.constructor=a;a.fp=function(d,e,f){for(var g=Array(arguments.length-2),h=2;h<arguments.length;h++)g[h-2]=arguments[h];return b.prototype[e].apply(d,g)}}
function Za(a){return a}
;function $a(a,b){if(Error.captureStackTrace)Error.captureStackTrace(this,$a);else{var c=Error().stack;c&&(this.stack=c)}a&&(this.message=String(a));void 0!==b&&(this.Nb=b)}
Ya($a,Error);$a.prototype.name="CustomError";function ab(a){a=a.url;var b=/[?&]dsh=1(&|$)/.test(a);this.l=!b&&/[?&]ae=1(&|$)/.test(a);this.m=!b&&/[?&]ae=2(&|$)/.test(a);if((this.i=/[?&]adurl=([^&]*)/.exec(a))&&this.i[1]){try{var c=decodeURIComponent(this.i[1])}catch(d){c=null}this.j=c}}
;function bb(){}
function cb(a){var b=!1,c;return function(){b||(c=a(),b=!0);return c}}
;var db=Array.prototype.indexOf?function(a,b){return Array.prototype.indexOf.call(a,b,void 0)}:function(a,b){if("string"===typeof a)return"string"!==typeof b||1!=b.length?-1:a.indexOf(b,0);
for(var c=0;c<a.length;c++)if(c in a&&a[c]===b)return c;return-1},eb=Array.prototype.forEach?function(a,b,c){Array.prototype.forEach.call(a,b,c)}:function(a,b,c){for(var d=a.length,e="string"===typeof a?a.split(""):a,f=0;f<d;f++)f in e&&b.call(c,e[f],f,a)},fb=Array.prototype.filter?function(a,b){return Array.prototype.filter.call(a,b,void 0)}:function(a,b){for(var c=a.length,d=[],e=0,f="string"===typeof a?a.split(""):a,g=0;g<c;g++)if(g in f){var h=f[g];
b.call(void 0,h,g,a)&&(d[e++]=h)}return d},gb=Array.prototype.map?function(a,b){return Array.prototype.map.call(a,b,void 0)}:function(a,b){for(var c=a.length,d=Array(c),e="string"===typeof a?a.split(""):a,f=0;f<c;f++)f in e&&(d[f]=b.call(void 0,e[f],f,a));
return d},hb=Array.prototype.reduce?function(a,b,c){return Array.prototype.reduce.call(a,b,c)}:function(a,b,c){var d=c;
eb(a,function(e,f){d=b.call(void 0,d,e,f,a)});
return d};
function ib(a,b){a:{for(var c=a.length,d="string"===typeof a?a.split(""):a,e=0;e<c;e++)if(e in d&&b.call(void 0,d[e],e,a)){b=e;break a}b=-1}return 0>b?null:"string"===typeof a?a.charAt(b):a[b]}
function jb(a,b){b=db(a,b);var c;(c=0<=b)&&Array.prototype.splice.call(a,b,1);return c}
function kb(a,b){for(var c=1;c<arguments.length;c++){var d=arguments[c];if(Pa(d)){var e=a.length||0,f=d.length||0;a.length=e+f;for(var g=0;g<f;g++)a[e+g]=d[g]}else a.push(d)}}
;function lb(a,b){for(var c in a)b.call(void 0,a[c],c,a)}
function mb(a){var b=nb,c;for(c in b)if(a.call(void 0,b[c],c,b))return c}
function ob(a){for(var b in a)return!1;return!0}
function pb(a,b){if(null!==a&&b in a)throw Error('The object already contains the key "'+b+'"');a[b]=!0}
function qb(a){return null!==a&&"privembed"in a?a.privembed:!1}
function tb(a,b){for(var c in a)if(!(c in b)||a[c]!==b[c])return!1;for(var d in b)if(!(d in a))return!1;return!0}
function ub(a){var b={},c;for(c in a)b[c]=a[c];return b}
function vb(a){if(!a||"object"!==typeof a)return a;if("function"===typeof a.clone)return a.clone();if("undefined"!==typeof Map&&a instanceof Map)return new Map(a);if("undefined"!==typeof Set&&a instanceof Set)return new Set(a);var b=Array.isArray(a)?[]:"function"!==typeof ArrayBuffer||"function"!==typeof ArrayBuffer.isView||!ArrayBuffer.isView(a)||a instanceof DataView?{}:new a.constructor(a.length),c;for(c in a)b[c]=vb(a[c]);return b}
var wb="constructor hasOwnProperty isPrototypeOf propertyIsEnumerable toLocaleString toString valueOf".split(" ");function xb(a,b){for(var c,d,e=1;e<arguments.length;e++){d=arguments[e];for(c in d)a[c]=d[c];for(var f=0;f<wb.length;f++)c=wb[f],Object.prototype.hasOwnProperty.call(d,c)&&(a[c]=d[c])}}
;var yb;function zb(){if(void 0===yb){var a=null,b=y.trustedTypes;if(b&&b.createPolicy){try{a=b.createPolicy("goog#html",{createHTML:Za,createScript:Za,createScriptURL:Za})}catch(c){y.console&&y.console.error(c.message)}yb=a}else yb=a}return yb}
;function Ab(a,b){this.l=a===Bb&&b||""}
Ab.prototype.j=!0;Ab.prototype.i=function(){return this.l};
function Cb(a){return new Ab(Bb,a)}
var Bb={};Cb("");var Db={};function Eb(a){this.l=Db===Db?a:"";this.j=!0}
Eb.prototype.i=function(){return this.l.toString()};
Eb.prototype.toString=function(){return this.l.toString()};function Fb(a,b){this.l=b===Gb?a:""}
Fb.prototype.j=!0;Fb.prototype.i=function(){return this.l.toString()};
Fb.prototype.toString=function(){return this.l+""};
function Hb(a){if(a instanceof Fb&&a.constructor===Fb)return a.l;Oa(a);return"type_error:TrustedResourceUrl"}
var Gb={};function Ib(a){var b=zb();a=b?b.createScriptURL(a):a;return new Fb(a,Gb)}
;var Jb=String.prototype.trim?function(a){return a.trim()}:function(a){return/^[\s\xa0]*([\s\S]*?)[\s\xa0]*$/.exec(a)[1]};function Kb(a,b){this.l=b===Nb?a:""}
Kb.prototype.j=!0;Kb.prototype.i=function(){return this.l.toString()};
Kb.prototype.toString=function(){return this.l.toString()};
function Ob(a){if(a instanceof Kb&&a.constructor===Kb)return a.l;Oa(a);return"type_error:SafeUrl"}
var Pb=/^(?:(?:https?|mailto|ftp):|[^:/?#]*(?:[/?#]|$))/i,Nb={};function Qb(){var a=y.navigator;return a&&(a=a.userAgent)?a:""}
function B(a){return-1!=Qb().indexOf(a)}
;function Rb(){return(B("Chrome")||B("CriOS"))&&!B("Edge")||B("Silk")}
;var Sb={};function Tb(a){this.l=Sb===Sb?a:"";this.j=!0}
Tb.prototype.i=function(){return this.l.toString()};
Tb.prototype.toString=function(){return this.l.toString()};function Ub(a,b){b instanceof Kb||b instanceof Kb||(b="object"==typeof b&&b.j?b.i():String(b),Pb.test(b)||(b="about:invalid#zClosurez"),b=new Kb(b,Nb));a.href=Ob(b)}
function Vb(a,b){a.rel="stylesheet";a.href=Hb(b).toString();(b=Wb('style[nonce],link[rel="stylesheet"][nonce]',a.ownerDocument&&a.ownerDocument.defaultView))&&a.setAttribute("nonce",b)}
function Xb(){return Wb("script[nonce]")}
var Yb=/^[\w+/_-]+[=]{0,2}$/;function Wb(a,b){b=(b||y).document;return b.querySelector?(a=b.querySelector(a))&&(a=a.nonce||a.getAttribute("nonce"))&&Yb.test(a)?a:"":""}
;function Zb(a){for(var b=0,c=0;c<a.length;++c)b=31*b+a.charCodeAt(c)>>>0;return b}
;var $b=RegExp("^(?:([^:/?#.]+):)?(?://(?:([^\\\\/?#]*)@)?([^\\\\/?#]*?)(?::([0-9]+))?(?=[\\\\/?#]|$))?([^?#]+)?(?:\\?([^#]*))?(?:#([\\s\\S]*))?$");function ac(a){return a?decodeURI(a):a}
function bc(a,b){return b.match($b)[a]||null}
function cc(a){return ac(bc(3,a))}
function dc(a){var b=a.match($b);a=b[5];var c=b[6];b=b[7];var d="";a&&(d+=a);c&&(d+="?"+c);b&&(d+="#"+b);return d}
function ec(a,b,c){if(Array.isArray(b))for(var d=0;d<b.length;d++)ec(a,String(b[d]),c);else null!=b&&c.push(a+(""===b?"":"="+encodeURIComponent(String(b))))}
function fc(a){var b=[],c;for(c in a)ec(c,a[c],b);return b.join("&")}
function gc(a,b){b=fc(b);if(b){var c=a.indexOf("#");0>c&&(c=a.length);var d=a.indexOf("?");if(0>d||d>c){d=c;var e=""}else e=a.substring(d+1,c);a=[a.slice(0,d),e,a.slice(c)];c=a[1];a[1]=b?c?c+"&"+b:b:c;b=a[0]+(a[1]?"?"+a[1]:"")+a[2]}else b=a;return b}
function hc(a,b,c,d){for(var e=c.length;0<=(b=a.indexOf(c,b))&&b<d;){var f=a.charCodeAt(b-1);if(38==f||63==f)if(f=a.charCodeAt(b+e),!f||61==f||38==f||35==f)return b;b+=e+1}return-1}
var ic=/#|$/,jc=/[?&]($|#)/;function nc(a){for(var b=a.search(ic),c=0,d,e=[];0<=(d=hc(a,c,"key",b));)e.push(a.substring(c,d)),c=Math.min(a.indexOf("&",d)+1||b,b);e.push(a.slice(c));return e.join("").replace(jc,"$1")}
;var oc={};function pc(){return B("iPhone")&&!B("iPod")&&!B("iPad")}
;function qc(a){qc[" "](a);return a}
qc[" "]=function(){};var rc=B("Opera"),sc=B("Trident")||B("MSIE"),tc=B("Edge"),uc=B("Gecko")&&!(-1!=Qb().toLowerCase().indexOf("webkit")&&!B("Edge"))&&!(B("Trident")||B("MSIE"))&&!B("Edge"),vc=-1!=Qb().toLowerCase().indexOf("webkit")&&!B("Edge"),wc=B("Android");function xc(){var a=y.document;return a?a.documentMode:void 0}
var yc;a:{var zc="",Ac=function(){var a=Qb();if(uc)return/rv:([^\);]+)(\)|;)/.exec(a);if(tc)return/Edge\/([\d\.]+)/.exec(a);if(sc)return/\b(?:MSIE|rv)[: ]([^\);]+)(\)|;)/.exec(a);if(vc)return/WebKit\/(\S+)/.exec(a);if(rc)return/(?:Version)[ \/]?(\S+)/.exec(a)}();
Ac&&(zc=Ac?Ac[1]:"");if(sc){var Bc=xc();if(null!=Bc&&Bc>parseFloat(zc)){yc=String(Bc);break a}}yc=zc}var Cc=yc,Dc;if(y.document&&sc){var Ec=xc();Dc=Ec?Ec:parseInt(Cc,10)||void 0}else Dc=void 0;var Fc=Dc;var Gc=pc()||B("iPod"),Hc=B("iPad");!B("Android")||Rb();Rb();var Ic=B("Safari")&&!(Rb()||B("Coast")||B("Opera")||B("Edge")||B("Edg/")||B("OPR")||B("Firefox")||B("FxiOS")||B("Silk")||B("Android"))&&!(pc()||B("iPad")||B("iPod"));var Jc={},Kc=null;
function Lc(a,b){Pa(a);void 0===b&&(b=0);if(!Kc){Kc={};for(var c="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".split(""),d=["+/=","+/","-_=","-_.","-_"],e=0;5>e;e++){var f=c.concat(d[e].split(""));Jc[e]=f;for(var g=0;g<f.length;g++){var h=f[g];void 0===Kc[h]&&(Kc[h]=g)}}}b=Jc[b];c=Array(Math.floor(a.length/3));d=b[64]||"";for(e=f=0;f<a.length-2;f+=3){var k=a[f],m=a[f+1];h=a[f+2];g=b[k>>2];k=b[(k&3)<<4|m>>4];m=b[(m&15)<<2|h>>6];h=b[h&63];c[e++]=""+g+k+m+h}g=0;h=d;switch(a.length-
f){case 2:g=a[f+1],h=b[(g&15)<<2]||d;case 1:a=a[f],c[e]=""+b[a>>2]+b[(a&3)<<4|g>>4]+h+d}return c.join("")}
;var Mc="undefined"!==typeof Uint8Array;var Nc="function"===typeof Symbol&&"symbol"===typeof Symbol()?Symbol(void 0):void 0;function Oc(a,b){Object.isFrozen(a)||(Nc?a[Nc]|=b:void 0!==a.La?a.La|=b:Object.defineProperties(a,{La:{value:b,configurable:!0,writable:!0,enumerable:!1}}))}
function Pc(a){var b;Nc?b=a[Nc]:b=a.La;return null==b?0:b}
function Qc(a){Oc(a,1);return a}
function Rc(a){return Array.isArray(a)?!!(Pc(a)&2):!1}
function Sc(a){if(!Array.isArray(a))throw Error("cannot mark non-array as immutable");Oc(a,2)}
;function Tc(a){return null!==a&&"object"===typeof a&&!Array.isArray(a)&&a.constructor===Object}
var Uc,Yc=Object.freeze(Qc([]));function Zc(a){if(Rc(a.F))throw Error("Cannot mutate an immutable Message");}
var $c="undefined"!=typeof Symbol&&"undefined"!=typeof Symbol.hasInstance;function ad(a){return{value:a,configurable:!1,writable:!1,enumerable:!1}}
;function bd(a){y.setTimeout(function(){throw a;},0)}
;function cd(a,b,c){c=void 0===c?!1:c;if(Array.isArray(a))return new b(a);if(c)return new b}
;function dd(a){switch(typeof a){case "number":return isFinite(a)?a:String(a);case "object":if(a&&!Array.isArray(a)&&Mc&&null!=a&&a instanceof Uint8Array)return Lc(a)}return a}
;function ed(a,b){b=void 0===b?fd:b;return gd(a,b)}
function hd(a,b){if(null!=a){if(Array.isArray(a))a=gd(a,b);else if(Tc(a)){var c={},d;for(d in a)c[d]=hd(a[d],b);a=c}else a=b(a);return a}}
function gd(a,b){for(var c=a.slice(),d=0;d<c.length;d++)c[d]=hd(c[d],b);Array.isArray(a)&&Pc(a)&1&&Qc(c);return c}
function id(a){if(a&&"object"==typeof a&&a.toJSON)return a.toJSON();a=dd(a);return Array.isArray(a)?ed(a,id):a}
function fd(a){return Mc&&null!=a&&a instanceof Uint8Array?new Uint8Array(a):a}
;function jd(a,b,c){return-1===b?null:b>=a.m?a.j?a.j[b]:void 0:(void 0===c?0:c)&&a.j&&(c=a.j[b],null!=c)?c:a.F[b+a.l]}
function C(a,b,c,d,e){d=void 0===d?!1:d;(void 0===e?0:e)||Zc(a);b<a.m&&!d?a.F[b+a.l]=c:(a.j||(a.j=a.F[a.m+a.l]={}))[b]=c;return a}
function kd(a,b,c,d){c=void 0===c?!0:c;var e=jd(a,b,d);null==e&&(e=Yc);if(Rc(a.F))c&&(Sc(e),Object.freeze(e));else if(e===Yc||Rc(e))e=Qc(e.slice()),C(a,b,e,d);return e}
function ld(a,b,c,d){Zc(a);(c=md(a,c))&&c!==b&&null!=d&&(a.i&&c in a.i&&(a.i[c]=void 0),C(a,c));return C(a,b,d)}
function md(a,b){for(var c=0,d=0;d<b.length;d++){var e=b[d];null!=jd(a,e)&&(0!==c&&C(a,c,void 0,!1,!0),c=e)}return c}
function nd(a,b,c,d,e){if(-1===c)return null;a.i||(a.i={});var f=a.i[c];if(f)return f;var g=jd(a,c,e);b=cd(g,b,d);if(void 0==b)return f;d&&b.F!==g&&C(a,c,b.F,e,!0);a.i[c]=b;Rc(a.F)&&Sc(b.F);return b}
function od(a,b,c,d){a.i||(a.i={});var e=Rc(a.F),f=a.i[c];if(!f){d=kd(a,c,!0,d);f=[];e=e||Rc(d);for(var g=0;g<d.length;g++){var h=cd(d[g],b);void 0!==h&&(f.push(h),e&&Sc(h.F))}e&&(Sc(f),Object.freeze(f));a.i[c]=f}return f}
function D(a,b,c){Zc(a);a.i||(a.i={});var d=null!=c?c.F:c;a.i[b]=c;return C(a,b,d)}
function pd(a,b,c,d){Zc(a);a.i||(a.i={});var e=null!=d?d.F:d;a.i[b]=d;ld(a,b,c,e)}
function qd(a,b,c,d){Zc(a);var e=od(a,c,b);c=null!=d?d:new c;a=kd(a,b);e.push(c);a.push(c.F)}
function rd(a,b){a=jd(a,b);return null==a?"":a}
;function sd(a,b,c){a||(a=td);td=null;var d=this.constructor.j;a||(a=d?[d]:[]);this.l=(d?0:-1)-(this.constructor.i||0);this.i=void 0;this.F=a;a:{d=this.F.length;a=d-1;if(d&&(d=this.F[a],Tc(d))){this.m=a-this.l;this.j=d;break a}void 0!==b&&-1<b?(this.m=Math.max(b,a+1-this.l),this.j=void 0):this.m=Number.MAX_VALUE}if(c)for(b=0;b<c.length;b++)if(a=c[b],a<this.m)a+=this.l,(d=this.F[a])?Array.isArray(d)&&Qc(d):this.F[a]=Yc;else{d=this.j||(this.j=this.F[this.m+this.l]={});var e=d[a];e?Array.isArray(e)&&
Qc(e):d[a]=Yc}}
sd.prototype.toJSON=function(){var a=this.F;return Uc?a:ed(a,id)};
sd.prototype.clone=function(){var a=ed(this.F);td=a;a=new this.constructor(a);td=null;ud(a,this);return a};
sd.prototype.isMutable=function(a){if(a!==oc)throw Error("requires a valid immutable API token");return!Rc(this.F)};
sd.prototype.toString=function(){return this.F.toString()};
function vd(a,b){return dd(b)}
function ud(a,b){b.s&&(a.s=b.s.slice());var c=b.i;if(c){b=b.j;for(var d in c){var e=c[d];if(e){var f=!(!b||!b[d]),g=+d;if(Array.isArray(e)){if(e.length)for(f=od(a,e[0].constructor,g,f),g=0;g<Math.min(f.length,e.length);g++)ud(f[g],e[g])}else(f=nd(a,e.constructor,g,void 0,f))&&ud(f,e)}}}}
var td;function wd(){sd.apply(this,arguments)}
r(wd,sd);if($c){var xd={};Object.defineProperties(wd,(xd[Symbol.hasInstance]=ad(function(){throw Error("Cannot perform instanceof checks for MutableMessage");}),xd))};function I(){wd.apply(this,arguments)}
r(I,wd);if($c){var yd={};Object.defineProperties(I,(yd[Symbol.hasInstance]=ad(Object[Symbol.hasInstance]),yd))};function zd(){var a=this;this.promise=new Promise(function(b,c){a.resolve=b;a.reject=c})}
;function Ad(a){this.j=!1;var b=a.program;a=a.globalName;var c=new zd;this.l=c.promise;this.m=q((0,y[a].a)(b,function(d,e){Promise.resolve().then(function(){c.resolve({Mb:d,vc:e})})},!0)).next().value;
this.uc=c.promise.then(function(){})}
Ad.prototype.snapshot=function(a){if(this.j)throw Error("Already disposed");return this.l.then(function(b){var c=b.Mb;return new Promise(function(d){c(function(e){d(e)},[a.nb,
a.wc])})})};
Ad.prototype.dispose=function(){this.j=!0;this.l.then(function(a){(a=a.vc)&&a()})};
Ad.prototype.i=function(){return this.j};var Bd=window;Cb("csi.gstatic.com");Cb("googleads.g.doubleclick.net");Cb("partner.googleadservices.com");Cb("pubads.g.doubleclick.net");Cb("securepubads.g.doubleclick.net");Cb("tpc.googlesyndication.com");/*

 SPDX-License-Identifier: Apache-2.0
*/
var Cd={};function Dd(){}
function Ed(a){this.i=a}
r(Ed,Dd);Ed.prototype.toString=function(){return this.i};
var Fd=new Ed("about:invalid#zTSz",Cd);function Gd(a){if(a instanceof Dd)if(a instanceof Ed)a=a.i;else throw Error("");else a=Ob(a);return a}
;function Hd(a,b){a.src=Hb(b);var c,d;(c=(b=null==(d=(c=(a.ownerDocument&&a.ownerDocument.defaultView||window).document).querySelector)?void 0:d.call(c,"script[nonce]"))?b.nonce||b.getAttribute("nonce")||"":"")&&a.setAttribute("nonce",c)}
;function Id(a,b){this.x=void 0!==a?a:0;this.y=void 0!==b?b:0}
l=Id.prototype;l.clone=function(){return new Id(this.x,this.y)};
l.equals=function(a){return a instanceof Id&&(this==a?!0:this&&a?this.x==a.x&&this.y==a.y:!1)};
l.ceil=function(){this.x=Math.ceil(this.x);this.y=Math.ceil(this.y);return this};
l.floor=function(){this.x=Math.floor(this.x);this.y=Math.floor(this.y);return this};
l.round=function(){this.x=Math.round(this.x);this.y=Math.round(this.y);return this};
l.scale=function(a,b){this.x*=a;this.y*="number"===typeof b?b:a;return this};function Jd(a,b){this.width=a;this.height=b}
l=Jd.prototype;l.clone=function(){return new Jd(this.width,this.height)};
l.aspectRatio=function(){return this.width/this.height};
l.isEmpty=function(){return!(this.width*this.height)};
l.ceil=function(){this.width=Math.ceil(this.width);this.height=Math.ceil(this.height);return this};
l.floor=function(){this.width=Math.floor(this.width);this.height=Math.floor(this.height);return this};
l.round=function(){this.width=Math.round(this.width);this.height=Math.round(this.height);return this};
l.scale=function(a,b){this.width*=a;this.height*="number"===typeof b?b:a;return this};function Kd(a){var b=document;return"string"===typeof a?b.getElementById(a):a}
function Ld(a){var b=document;a=String(a);"application/xhtml+xml"===b.contentType&&(a=a.toLowerCase());return b.createElement(a)}
function Md(a,b){for(var c=0;a;){if(b(a))return a;a=a.parentNode;c++}return null}
;function Nd(a){var b=Od;if(b)for(var c in b)Object.prototype.hasOwnProperty.call(b,c)&&a(b[c],c,b)}
function Pd(){var a=[];Nd(function(b){a.push(b)});
return a}
var Od={Nc:"allow-forms",Oc:"allow-modals",Pc:"allow-orientation-lock",Qc:"allow-pointer-lock",Rc:"allow-popups",Sc:"allow-popups-to-escape-sandbox",Tc:"allow-presentation",Uc:"allow-same-origin",Vc:"allow-scripts",Wc:"allow-top-navigation",Xc:"allow-top-navigation-by-user-activation"},Qd=cb(function(){return Pd()});
function Rd(){var a=Sd(),b={};eb(Qd(),function(c){a.sandbox&&a.sandbox.supports&&a.sandbox.supports(c)&&(b[c]=!0)});
return b}
function Sd(){var a=void 0===a?document:a;return a.createElement("iframe")}
;function Td(a){this.isValid=a}
function Xd(a){return new Td(function(b){return b.substr(0,a.length+1).toLowerCase()===a+":"})}
var Yd=[Xd("data"),Xd("http"),Xd("https"),Xd("mailto"),Xd("ftp"),new Td(function(a){return/^[^:]*([/?#]|$)/.test(a)})];function Zd(a){"number"==typeof a&&(a=Math.round(a)+"px");return a}
;var $d=(new Date).getTime();function ae(a){if(!a)return"";if(/^about:(?:blank|srcdoc)$/.test(a))return window.origin||"";a=a.split("#")[0].split("?")[0];a=a.toLowerCase();0==a.indexOf("//")&&(a=window.location.protocol+a);/^[\w\-]*:\/\//.test(a)||(a=window.location.href);var b=a.substring(a.indexOf("://")+3),c=b.indexOf("/");-1!=c&&(b=b.substring(0,c));c=a.substring(0,a.indexOf("://"));if(!c)throw Error("URI is missing protocol: "+a);if("http"!==c&&"https"!==c&&"chrome-extension"!==c&&"moz-extension"!==c&&"file"!==c&&"android-app"!==
c&&"chrome-search"!==c&&"chrome-untrusted"!==c&&"chrome"!==c&&"app"!==c&&"devtools"!==c)throw Error("Invalid URI scheme in origin: "+c);a="";var d=b.indexOf(":");if(-1!=d){var e=b.substring(d+1);b=b.substring(0,d);if("http"===c&&"80"!==e||"https"===c&&"443"!==e)a=":"+e}return c+"://"+b+a}
;var be="client_dev_mss_url client_dev_regex_map client_dev_root_url client_rollout_override expflag forcedCapability jsfeat jsmode mods".split(" ");fa(be);function ce(){function a(){e[0]=1732584193;e[1]=4023233417;e[2]=2562383102;e[3]=271733878;e[4]=3285377520;p=m=0}
function b(u){for(var x=g,v=0;64>v;v+=4)x[v/4]=u[v]<<24|u[v+1]<<16|u[v+2]<<8|u[v+3];for(v=16;80>v;v++)u=x[v-3]^x[v-8]^x[v-14]^x[v-16],x[v]=(u<<1|u>>>31)&4294967295;u=e[0];var E=e[1],G=e[2],H=e[3],R=e[4];for(v=0;80>v;v++){if(40>v)if(20>v){var N=H^E&(G^H);var S=1518500249}else N=E^G^H,S=1859775393;else 60>v?(N=E&G|H&(E|G),S=2400959708):(N=E^G^H,S=3395469782);N=((u<<5|u>>>27)&4294967295)+N+R+S+x[v]&4294967295;R=H;H=G;G=(E<<30|E>>>2)&4294967295;E=u;u=N}e[0]=e[0]+u&4294967295;e[1]=e[1]+E&4294967295;e[2]=
e[2]+G&4294967295;e[3]=e[3]+H&4294967295;e[4]=e[4]+R&4294967295}
function c(u,x){if("string"===typeof u){u=unescape(encodeURIComponent(u));for(var v=[],E=0,G=u.length;E<G;++E)v.push(u.charCodeAt(E));u=v}x||(x=u.length);v=0;if(0==m)for(;v+64<x;)b(u.slice(v,v+64)),v+=64,p+=64;for(;v<x;)if(f[m++]=u[v++],p++,64==m)for(m=0,b(f);v+64<x;)b(u.slice(v,v+64)),v+=64,p+=64}
function d(){var u=[],x=8*p;56>m?c(h,56-m):c(h,64-(m-56));for(var v=63;56<=v;v--)f[v]=x&255,x>>>=8;b(f);for(v=x=0;5>v;v++)for(var E=24;0<=E;E-=8)u[x++]=e[v]>>E&255;return u}
for(var e=[],f=[],g=[],h=[128],k=1;64>k;++k)h[k]=0;var m,p;a();return{reset:a,update:c,digest:d,Qb:function(){for(var u=d(),x="",v=0;v<u.length;v++)x+="0123456789ABCDEF".charAt(Math.floor(u[v]/16))+"0123456789ABCDEF".charAt(u[v]%16);return x}}}
;function de(a,b,c){var d=String(y.location.href);return d&&a&&b?[b,ee(ae(d),a,c||null)].join(" "):null}
function ee(a,b,c){var d=[],e=[];if(1==(Array.isArray(c)?2:1))return e=[b,a],eb(d,function(h){e.push(h)}),fe(e.join(" "));
var f=[],g=[];eb(c,function(h){g.push(h.key);f.push(h.value)});
c=Math.floor((new Date).getTime()/1E3);e=0==f.length?[c,b,a]:[f.join(":"),c,b,a];eb(d,function(h){e.push(h)});
a=fe(e.join(" "));a=[c,a];0==g.length||a.push(g.join(""));return a.join("_")}
function fe(a){var b=ce();b.update(a);return b.Qb().toLowerCase()}
;var ge={};function he(a){this.i=a||{cookie:""}}
l=he.prototype;l.isEnabled=function(){if(!y.navigator.cookieEnabled)return!1;if(!this.isEmpty())return!0;this.set("TESTCOOKIESENABLED","1",{Na:60});if("1"!==this.get("TESTCOOKIESENABLED"))return!1;this.remove("TESTCOOKIESENABLED");return!0};
l.set=function(a,b,c){var d=!1;if("object"===typeof c){var e=c.vp;d=c.secure||!1;var f=c.domain||void 0;var g=c.path||void 0;var h=c.Na}if(/[;=\s]/.test(a))throw Error('Invalid cookie name "'+a+'"');if(/[;\r\n]/.test(b))throw Error('Invalid cookie value "'+b+'"');void 0===h&&(h=-1);c=f?";domain="+f:"";g=g?";path="+g:"";d=d?";secure":"";h=0>h?"":0==h?";expires="+(new Date(1970,1,1)).toUTCString():";expires="+(new Date(Date.now()+1E3*h)).toUTCString();this.i.cookie=a+"="+b+c+g+h+d+(null!=e?";samesite="+
e:"")};
l.get=function(a,b){for(var c=a+"=",d=(this.i.cookie||"").split(";"),e=0,f;e<d.length;e++){f=Jb(d[e]);if(0==f.lastIndexOf(c,0))return f.slice(c.length);if(f==a)return""}return b};
l.remove=function(a,b,c){var d=void 0!==this.get(a);this.set(a,"",{Na:0,path:b,domain:c});return d};
l.Xa=function(){return ie(this).keys};
l.isEmpty=function(){return!this.i.cookie};
l.clear=function(){for(var a=ie(this).keys,b=a.length-1;0<=b;b--)this.remove(a[b])};
function ie(a){a=(a.i.cookie||"").split(";");for(var b=[],c=[],d,e,f=0;f<a.length;f++)e=Jb(a[f]),d=e.indexOf("="),-1==d?(b.push(""),c.push(e)):(b.push(e.substring(0,d)),c.push(e.substring(d+1)));return{keys:b,values:c}}
var je=new he("undefined"==typeof document?null:document);function ke(a){return!!ge.FPA_SAMESITE_PHASE2_MOD||!(void 0===a||!a)}
function le(a){a=void 0===a?!1:a;var b=y.__SAPISID||y.__APISID||y.__3PSAPISID||y.__OVERRIDE_SID;ke(a)&&(b=b||y.__1PSAPISID);if(b)return!0;var c=new he(document);b=c.get("SAPISID")||c.get("APISID")||c.get("__Secure-3PAPISID")||c.get("SID");ke(a)&&(b=b||c.get("__Secure-1PAPISID"));return!!b}
function me(a,b,c,d){(a=y[a])||(a=(new he(document)).get(b));return a?de(a,c,d):null}
function ne(a){var b=void 0===b?!1:b;var c=ae(String(y.location.href)),d=[];if(le(b)){c=0==c.indexOf("https:")||0==c.indexOf("chrome-extension:")||0==c.indexOf("moz-extension:");var e=c?y.__SAPISID:y.__APISID;e||(e=new he(document),e=e.get(c?"SAPISID":"APISID")||e.get("__Secure-3PAPISID"));(e=e?de(e,c?"SAPISIDHASH":"APISIDHASH",a):null)&&d.push(e);c&&ke(b)&&((b=me("__1PSAPISID","__Secure-1PAPISID","SAPISID1PHASH",a))&&d.push(b),(a=me("__3PSAPISID","__Secure-3PAPISID","SAPISID3PHASH",a))&&d.push(a))}return 0==
d.length?null:d.join(" ")}
;function oe(a){a&&"function"==typeof a.dispose&&a.dispose()}
;function pe(a){for(var b=0,c=arguments.length;b<c;++b){var d=arguments[b];Pa(d)?pe.apply(null,d):oe(d)}}
;function J(){this.D=this.D;this.s=this.s}
J.prototype.D=!1;J.prototype.i=function(){return this.D};
J.prototype.dispose=function(){this.D||(this.D=!0,this.I())};
function qe(a,b){a.D?b():(a.s||(a.s=[]),a.s.push(b))}
J.prototype.I=function(){if(this.s)for(;this.s.length;)this.s.shift()()};function re(a,b){this.type=a;this.i=this.target=b;this.defaultPrevented=this.l=!1}
re.prototype.stopPropagation=function(){this.l=!0};
re.prototype.preventDefault=function(){this.defaultPrevented=!0};function se(a){var b=A("window.location.href");null==a&&(a='Unknown Error of type "null/undefined"');if("string"===typeof a)return{message:a,name:"Unknown error",lineNumber:"Not available",fileName:b,stack:"Not available"};var c=!1;try{var d=a.lineNumber||a.line||"Not available"}catch(g){d="Not available",c=!0}try{var e=a.fileName||a.filename||a.sourceURL||y.$googDebugFname||b}catch(g){e="Not available",c=!0}b=te(a);if(!(!c&&a.lineNumber&&a.fileName&&a.stack&&a.message&&a.name)){c=a.message;if(null==
c){if(a.constructor&&a.constructor instanceof Function){if(a.constructor.name)c=a.constructor.name;else if(c=a.constructor,ue[c])c=ue[c];else{c=String(c);if(!ue[c]){var f=/function\s+([^\(]+)/m.exec(c);ue[c]=f?f[1]:"[Anonymous]"}c=ue[c]}c='Unknown Error of type "'+c+'"'}else c="Unknown Error of unknown type";"function"===typeof a.toString&&Object.prototype.toString!==a.toString&&(c+=": "+a.toString())}return{message:c,name:a.name||"UnknownError",lineNumber:d,fileName:e,stack:b||"Not available"}}a.stack=
b;return{message:a.message,name:a.name,lineNumber:a.lineNumber,fileName:a.fileName,stack:a.stack}}
function te(a,b){b||(b={});b[ve(a)]=!0;var c=a.stack||"";(a=a.Nb)&&!b[ve(a)]&&(c+="\nCaused by: ",a.stack&&0==a.stack.indexOf(a.toString())||(c+="string"===typeof a?a:a.message+"\n"),c+=te(a,b));return c}
function ve(a){var b="";"function"===typeof a.toString&&(b=""+a);return b+a.stack}
var ue={};var we=function(){if(!y.addEventListener||!Object.defineProperty)return!1;var a=!1,b=Object.defineProperty({},"passive",{get:function(){a=!0}});
try{y.addEventListener("test",function(){},b),y.removeEventListener("test",function(){},b)}catch(c){}return a}();function xe(a,b){re.call(this,a?a.type:"");this.relatedTarget=this.i=this.target=null;this.button=this.screenY=this.screenX=this.clientY=this.clientX=0;this.key="";this.charCode=this.keyCode=0;this.metaKey=this.shiftKey=this.altKey=this.ctrlKey=!1;this.state=null;this.pointerId=0;this.pointerType="";this.j=null;a&&this.init(a,b)}
Ya(xe,re);var ye={2:"touch",3:"pen",4:"mouse"};
xe.prototype.init=function(a,b){var c=this.type=a.type,d=a.changedTouches&&a.changedTouches.length?a.changedTouches[0]:null;this.target=a.target||a.srcElement;this.i=b;if(b=a.relatedTarget){if(uc){a:{try{qc(b.nodeName);var e=!0;break a}catch(f){}e=!1}e||(b=null)}}else"mouseover"==c?b=a.fromElement:"mouseout"==c&&(b=a.toElement);this.relatedTarget=b;d?(this.clientX=void 0!==d.clientX?d.clientX:d.pageX,this.clientY=void 0!==d.clientY?d.clientY:d.pageY,this.screenX=d.screenX||0,this.screenY=d.screenY||
0):(this.clientX=void 0!==a.clientX?a.clientX:a.pageX,this.clientY=void 0!==a.clientY?a.clientY:a.pageY,this.screenX=a.screenX||0,this.screenY=a.screenY||0);this.button=a.button;this.keyCode=a.keyCode||0;this.key=a.key||"";this.charCode=a.charCode||("keypress"==c?a.keyCode:0);this.ctrlKey=a.ctrlKey;this.altKey=a.altKey;this.shiftKey=a.shiftKey;this.metaKey=a.metaKey;this.pointerId=a.pointerId||0;this.pointerType="string"===typeof a.pointerType?a.pointerType:ye[a.pointerType]||"";this.state=a.state;
this.j=a;a.defaultPrevented&&xe.Y.preventDefault.call(this)};
xe.prototype.stopPropagation=function(){xe.Y.stopPropagation.call(this);this.j.stopPropagation?this.j.stopPropagation():this.j.cancelBubble=!0};
xe.prototype.preventDefault=function(){xe.Y.preventDefault.call(this);var a=this.j;a.preventDefault?a.preventDefault():a.returnValue=!1};var ze="closure_listenable_"+(1E6*Math.random()|0);var Ae=0;function Be(a,b,c,d,e){this.listener=a;this.proxy=null;this.src=b;this.type=c;this.capture=!!d;this.Ka=e;this.key=++Ae;this.wa=this.Ga=!1}
function Ce(a){a.wa=!0;a.listener=null;a.proxy=null;a.src=null;a.Ka=null}
;function De(a){this.src=a;this.listeners={};this.i=0}
De.prototype.add=function(a,b,c,d,e){var f=a.toString();a=this.listeners[f];a||(a=this.listeners[f]=[],this.i++);var g=Ee(a,b,d,e);-1<g?(b=a[g],c||(b.Ga=!1)):(b=new Be(b,this.src,f,!!d,e),b.Ga=c,a.push(b));return b};
De.prototype.remove=function(a,b,c,d){a=a.toString();if(!(a in this.listeners))return!1;var e=this.listeners[a];b=Ee(e,b,c,d);return-1<b?(Ce(e[b]),Array.prototype.splice.call(e,b,1),0==e.length&&(delete this.listeners[a],this.i--),!0):!1};
function Fe(a,b){var c=b.type;c in a.listeners&&jb(a.listeners[c],b)&&(Ce(b),0==a.listeners[c].length&&(delete a.listeners[c],a.i--))}
function Ee(a,b,c,d){for(var e=0;e<a.length;++e){var f=a[e];if(!f.wa&&f.listener==b&&f.capture==!!c&&f.Ka==d)return e}return-1}
;var Ge="closure_lm_"+(1E6*Math.random()|0),He={},Ie=0;function Je(a,b,c,d,e){if(d&&d.once)Ke(a,b,c,d,e);else if(Array.isArray(b))for(var f=0;f<b.length;f++)Je(a,b[f],c,d,e);else c=Le(c),a&&a[ze]?a.U(b,c,Qa(d)?!!d.capture:!!d,e):Me(a,b,c,!1,d,e)}
function Me(a,b,c,d,e,f){if(!b)throw Error("Invalid event type");var g=Qa(e)?!!e.capture:!!e,h=Ne(a);h||(a[Ge]=h=new De(a));c=h.add(b,c,d,g,f);if(!c.proxy){d=Oe();c.proxy=d;d.src=a;d.listener=c;if(a.addEventListener)we||(e=g),void 0===e&&(e=!1),a.addEventListener(b.toString(),d,e);else if(a.attachEvent)a.attachEvent(Pe(b.toString()),d);else if(a.addListener&&a.removeListener)a.addListener(d);else throw Error("addEventListener and attachEvent are unavailable.");Ie++}}
function Oe(){function a(c){return b.call(a.src,a.listener,c)}
var b=Qe;return a}
function Ke(a,b,c,d,e){if(Array.isArray(b))for(var f=0;f<b.length;f++)Ke(a,b[f],c,d,e);else c=Le(c),a&&a[ze]?a.m.add(String(b),c,!0,Qa(d)?!!d.capture:!!d,e):Me(a,b,c,!0,d,e)}
function Re(a,b,c,d,e){if(Array.isArray(b))for(var f=0;f<b.length;f++)Re(a,b[f],c,d,e);else(d=Qa(d)?!!d.capture:!!d,c=Le(c),a&&a[ze])?a.m.remove(String(b),c,d,e):a&&(a=Ne(a))&&(b=a.listeners[b.toString()],a=-1,b&&(a=Ee(b,c,d,e)),(c=-1<a?b[a]:null)&&Se(c))}
function Se(a){if("number"!==typeof a&&a&&!a.wa){var b=a.src;if(b&&b[ze])Fe(b.m,a);else{var c=a.type,d=a.proxy;b.removeEventListener?b.removeEventListener(c,d,a.capture):b.detachEvent?b.detachEvent(Pe(c),d):b.addListener&&b.removeListener&&b.removeListener(d);Ie--;(c=Ne(b))?(Fe(c,a),0==c.i&&(c.src=null,b[Ge]=null)):Ce(a)}}}
function Pe(a){return a in He?He[a]:He[a]="on"+a}
function Qe(a,b){if(a.wa)a=!0;else{b=new xe(b,this);var c=a.listener,d=a.Ka||a.src;a.Ga&&Se(a);a=c.call(d,b)}return a}
function Ne(a){a=a[Ge];return a instanceof De?a:null}
var Te="__closure_events_fn_"+(1E9*Math.random()>>>0);function Le(a){if("function"===typeof a)return a;a[Te]||(a[Te]=function(b){return a.handleEvent(b)});
return a[Te]}
;function Ue(){J.call(this);this.m=new De(this);this.W=this;this.L=null}
Ya(Ue,J);Ue.prototype[ze]=!0;Ue.prototype.addEventListener=function(a,b,c,d){Je(this,a,b,c,d)};
Ue.prototype.removeEventListener=function(a,b,c,d){Re(this,a,b,c,d)};
function Ve(a,b){var c=a.L;if(c){var d=[];for(var e=1;c;c=c.L)d.push(c),++e}a=a.W;c=b.type||b;"string"===typeof b?b=new re(b,a):b instanceof re?b.target=b.target||a:(e=b,b=new re(c,a),xb(b,e));e=!0;if(d)for(var f=d.length-1;!b.l&&0<=f;f--){var g=b.i=d[f];e=We(g,c,!0,b)&&e}b.l||(g=b.i=a,e=We(g,c,!0,b)&&e,b.l||(e=We(g,c,!1,b)&&e));if(d)for(f=0;!b.l&&f<d.length;f++)g=b.i=d[f],e=We(g,c,!1,b)&&e}
Ue.prototype.I=function(){Ue.Y.I.call(this);if(this.m){var a=this.m,b=0,c;for(c in a.listeners){for(var d=a.listeners[c],e=0;e<d.length;e++)++b,Ce(d[e]);delete a.listeners[c];a.i--}}this.L=null};
Ue.prototype.U=function(a,b,c,d){return this.m.add(String(a),b,!1,c,d)};
function We(a,b,c,d){b=a.m.listeners[String(b)];if(!b)return!0;b=b.concat();for(var e=!0,f=0;f<b.length;++f){var g=b[f];if(g&&!g.wa&&g.capture==c){var h=g.listener,k=g.Ka||g.src;g.Ga&&Fe(a.m,g);e=!1!==h.call(k,d)&&e}}return e&&!d.defaultPrevented}
;function Xe(a){Ue.call(this);var b=this;this.A=this.l=0;this.T=null!=a?a:{M:function(e,f){return setTimeout(e,f)},
S:function(e){clearTimeout(e)}};
var c,d;this.j=null!=(d=null==(c=window.navigator)?void 0:c.onLine)?d:!0;this.o=function(){return w(function(e){return t(e,Ye(b),0)})};
window.addEventListener("offline",this.o);window.addEventListener("online",this.o);this.A||Ze(this)}
r(Xe,Ue);function $e(){var a=af;Xe.i||(Xe.i=new Xe(a));return Xe.i}
Xe.prototype.dispose=function(){window.removeEventListener("offline",this.o);window.removeEventListener("online",this.o);this.T.S(this.A);delete Xe.i};
Xe.prototype.H=function(){return this.j};
function Ze(a){a.A=a.T.M(function(){var b;return w(function(c){if(1==c.i)return a.j?(null==(b=window.navigator)?0:b.onLine)?c.u(3):t(c,Ye(a),3):t(c,Ye(a),3);Ze(a);c.i=0})},3E4)}
function Ye(a,b){return a.v?a.v:a.v=new Promise(function(c){var d,e,f,g;return w(function(h){switch(h.i){case 1:return d=window.AbortController?new window.AbortController:void 0,f=null==(e=d)?void 0:e.signal,g=!1,wa(h,2,3),d&&(a.l=a.T.M(function(){d.abort()},b||2E4)),t(h,fetch("/generate_204",{method:"HEAD",
signal:f}),5);case 5:g=!0;case 3:za(h);a.v=void 0;a.l&&(a.T.S(a.l),a.l=0);g!==a.j&&(a.j=g,a.j?Ve(a,"networkstatus-online"):Ve(a,"networkstatus-offline"));c(g);Aa(h);break;case 2:ya(h),g=!1,h.u(3)}})})}
;var bf={Rg:"EMBEDDED_PLAYER_MODE_UNKNOWN",Og:"EMBEDDED_PLAYER_MODE_DEFAULT",Qg:"EMBEDDED_PLAYER_MODE_PFP",Pg:"EMBEDDED_PLAYER_MODE_PFL"},cf={Oo:"WEB_DISPLAY_MODE_UNKNOWN",Ko:"WEB_DISPLAY_MODE_BROWSER",Mo:"WEB_DISPLAY_MODE_MINIMAL_UI",No:"WEB_DISPLAY_MODE_STANDALONE",Lo:"WEB_DISPLAY_MODE_FULLSCREEN"};function df(){this.data_=[];this.i=-1}
df.prototype.set=function(a,b){b=void 0===b?!0:b;0<=a&&52>a&&Number.isInteger(a)&&this.data_[a]!==b&&(this.data_[a]=b,this.i=-1)};
df.prototype.get=function(a){return!!this.data_[a]};
function ef(a){-1===a.i&&(a.i=hb(a.data_,function(b,c,d){return c?b+Math.pow(2,d):b},0));
return a.i}
;function ff(a,b){this.l=a;this.m=b;this.j=0;this.i=null}
ff.prototype.get=function(){if(0<this.j){this.j--;var a=this.i;this.i=a.next;a.next=null}else a=this.l();return a};
function gf(a,b){a.m(b);100>a.j&&(a.j++,b.next=a.i,a.i=b)}
;var hf;function jf(){var a=y.MessageChannel;"undefined"===typeof a&&"undefined"!==typeof window&&window.postMessage&&window.addEventListener&&!B("Presto")&&(a=function(){var e=Ld("IFRAME");e.style.display="none";document.documentElement.appendChild(e);var f=e.contentWindow;e=f.document;e.open();e.close();var g="callImmediate"+Math.random(),h="file:"==f.location.protocol?"*":f.location.protocol+"//"+f.location.host;e=Wa(function(k){if(("*"==h||k.origin==h)&&k.data==g)this.port1.onmessage()},this);
f.addEventListener("message",e,!1);this.port1={};this.port2={postMessage:function(){f.postMessage(g,h)}}});
if("undefined"!==typeof a&&!B("Trident")&&!B("MSIE")){var b=new a,c={},d=c;b.port1.onmessage=function(){if(void 0!==c.next){c=c.next;var e=c.lb;c.lb=null;e()}};
return function(e){d.next={lb:e};d=d.next;b.port2.postMessage(0)}}return function(e){y.setTimeout(e,0)}}
;function kf(){this.j=this.i=null}
kf.prototype.add=function(a,b){var c=lf.get();c.set(a,b);this.j?this.j.next=c:this.i=c;this.j=c};
kf.prototype.remove=function(){var a=null;this.i&&(a=this.i,this.i=this.i.next,this.i||(this.j=null),a.next=null);return a};
var lf=new ff(function(){return new mf},function(a){return a.reset()});
function mf(){this.next=this.scope=this.i=null}
mf.prototype.set=function(a,b){this.i=a;this.scope=b;this.next=null};
mf.prototype.reset=function(){this.next=this.scope=this.i=null};function nf(a,b){of||pf();qf||(of(),qf=!0);rf.add(a,b)}
var of;function pf(){if(y.Promise&&y.Promise.resolve){var a=y.Promise.resolve(void 0);of=function(){a.then(sf)}}else of=function(){var b=sf;
"function"!==typeof y.setImmediate||y.Window&&y.Window.prototype&&!B("Edge")&&y.Window.prototype.setImmediate==y.setImmediate?(hf||(hf=jf()),hf(b)):y.setImmediate(b)}}
var qf=!1,rf=new kf;function sf(){for(var a;a=rf.remove();){try{a.i.call(a.scope)}catch(b){bd(b)}gf(lf,a)}qf=!1}
;function tf(a,b){this.i=a[y.Symbol.iterator]();this.j=b}
tf.prototype[Symbol.iterator]=function(){return this};
tf.prototype.next=function(){var a=this.i.next();return{value:a.done?void 0:this.j.call(void 0,a.value),done:a.done}};
function uf(a,b){return new tf(a,b)}
;function vf(){this.blockSize=-1}
;function wf(){this.blockSize=-1;this.blockSize=64;this.i=[];this.s=[];this.o=[];this.l=[];this.l[0]=128;for(var a=1;a<this.blockSize;++a)this.l[a]=0;this.m=this.j=0;this.reset()}
Ya(wf,vf);wf.prototype.reset=function(){this.i[0]=1732584193;this.i[1]=4023233417;this.i[2]=2562383102;this.i[3]=271733878;this.i[4]=3285377520;this.m=this.j=0};
function xf(a,b,c){c||(c=0);var d=a.o;if("string"===typeof b)for(var e=0;16>e;e++)d[e]=b.charCodeAt(c)<<24|b.charCodeAt(c+1)<<16|b.charCodeAt(c+2)<<8|b.charCodeAt(c+3),c+=4;else for(e=0;16>e;e++)d[e]=b[c]<<24|b[c+1]<<16|b[c+2]<<8|b[c+3],c+=4;for(e=16;80>e;e++){var f=d[e-3]^d[e-8]^d[e-14]^d[e-16];d[e]=(f<<1|f>>>31)&4294967295}b=a.i[0];c=a.i[1];var g=a.i[2],h=a.i[3],k=a.i[4];for(e=0;80>e;e++){if(40>e)if(20>e){f=h^c&(g^h);var m=1518500249}else f=c^g^h,m=1859775393;else 60>e?(f=c&g|h&(c|g),m=2400959708):
(f=c^g^h,m=3395469782);f=(b<<5|b>>>27)+f+k+m+d[e]&4294967295;k=h;h=g;g=(c<<30|c>>>2)&4294967295;c=b;b=f}a.i[0]=a.i[0]+b&4294967295;a.i[1]=a.i[1]+c&4294967295;a.i[2]=a.i[2]+g&4294967295;a.i[3]=a.i[3]+h&4294967295;a.i[4]=a.i[4]+k&4294967295}
wf.prototype.update=function(a,b){if(null!=a){void 0===b&&(b=a.length);for(var c=b-this.blockSize,d=0,e=this.s,f=this.j;d<b;){if(0==f)for(;d<=c;)xf(this,a,d),d+=this.blockSize;if("string"===typeof a)for(;d<b;){if(e[f]=a.charCodeAt(d),++f,++d,f==this.blockSize){xf(this,e);f=0;break}}else for(;d<b;)if(e[f]=a[d],++f,++d,f==this.blockSize){xf(this,e);f=0;break}}this.j=f;this.m+=b}};
wf.prototype.digest=function(){var a=[],b=8*this.m;56>this.j?this.update(this.l,56-this.j):this.update(this.l,this.blockSize-(this.j-56));for(var c=this.blockSize-1;56<=c;c--)this.s[c]=b&255,b/=256;xf(this,this.s);for(c=b=0;5>c;c++)for(var d=24;0<=d;d-=8)a[b]=this.i[c]>>d&255,++b;return a};function yf(a){return"string"==typeof a.className?a.className:a.getAttribute&&a.getAttribute("class")||""}
function zf(a,b){"string"==typeof a.className?a.className=b:a.setAttribute&&a.setAttribute("class",b)}
function Af(a,b){a.classList?b=a.classList.contains(b):(a=a.classList?a.classList:yf(a).match(/\S+/g)||[],b=0<=db(a,b));return b}
function Bf(){var a=document.body;a.classList?a.classList.remove("inverted-hdpi"):Af(a,"inverted-hdpi")&&zf(a,Array.prototype.filter.call(a.classList?a.classList:yf(a).match(/\S+/g)||[],function(b){return"inverted-hdpi"!=b}).join(" "))}
;function Cf(){}
Cf.prototype.next=function(){return Df};
var Df={done:!0,value:void 0};function Ef(a){return{value:a,done:!1}}
Cf.prototype.X=function(){return this};function Ff(a){if(a instanceof Gf||a instanceof Hf||a instanceof If)return a;if("function"==typeof a.next)return new Gf(function(){return a});
if("function"==typeof a[Symbol.iterator])return new Gf(function(){return a[Symbol.iterator]()});
if("function"==typeof a.X)return new Gf(function(){return a.X()});
throw Error("Not an iterator or iterable.");}
function Gf(a){this.j=a}
Gf.prototype.X=function(){return new Hf(this.j())};
Gf.prototype[Symbol.iterator]=function(){return new If(this.j())};
Gf.prototype.i=function(){return new If(this.j())};
function Hf(a){this.j=a}
r(Hf,Cf);Hf.prototype.next=function(){return this.j.next()};
Hf.prototype[Symbol.iterator]=function(){return new If(this.j)};
Hf.prototype.i=function(){return new If(this.j)};
function If(a){Gf.call(this,function(){return a});
this.l=a}
r(If,Gf);If.prototype.next=function(){return this.l.next()};function Jf(a,b){this.j={};this.i=[];this.ga=this.size=0;var c=arguments.length;if(1<c){if(c%2)throw Error("Uneven number of arguments");for(var d=0;d<c;d+=2)this.set(arguments[d],arguments[d+1])}else if(a)if(a instanceof Jf)for(c=a.Xa(),d=0;d<c.length;d++)this.set(c[d],a.get(c[d]));else for(d in a)this.set(d,a[d])}
l=Jf.prototype;l.Xa=function(){Kf(this);return this.i.concat()};
l.has=function(a){return Lf(this.j,a)};
l.equals=function(a,b){if(this===a)return!0;if(this.size!=a.size)return!1;b=b||Mf;Kf(this);for(var c,d=0;c=this.i[d];d++)if(!b(this.get(c),a.get(c)))return!1;return!0};
function Mf(a,b){return a===b}
l.isEmpty=function(){return 0==this.size};
l.clear=function(){this.j={};this.ga=this.size=this.i.length=0};
l.remove=function(a){return this.delete(a)};
l.delete=function(a){return Lf(this.j,a)?(delete this.j[a],--this.size,this.ga++,this.i.length>2*this.size&&Kf(this),!0):!1};
function Kf(a){if(a.size!=a.i.length){for(var b=0,c=0;b<a.i.length;){var d=a.i[b];Lf(a.j,d)&&(a.i[c++]=d);b++}a.i.length=c}if(a.size!=a.i.length){var e={};for(c=b=0;b<a.i.length;)d=a.i[b],Lf(e,d)||(a.i[c++]=d,e[d]=1),b++;a.i.length=c}}
l.get=function(a,b){return Lf(this.j,a)?this.j[a]:b};
l.set=function(a,b){Lf(this.j,a)||(this.size+=1,this.i.push(a),this.ga++);this.j[a]=b};
l.forEach=function(a,b){for(var c=this.Xa(),d=0;d<c.length;d++){var e=c[d],f=this.get(e);a.call(b,f,e,this)}};
l.clone=function(){return new Jf(this)};
l.keys=function(){return Ff(this.X(!0)).i()};
l.values=function(){return Ff(this.X(!1)).i()};
l.entries=function(){var a=this;return uf(this.keys(),function(b){return[b,a.get(b)]})};
l.X=function(a){Kf(this);var b=0,c=this.ga,d=this,e=new Cf;e.next=function(){if(c!=d.ga)throw Error("The map has changed since the iterator was created");if(b>=d.i.length)return Df;var f=d.i[b++];return Ef(a?f:d.j[f])};
return e};
function Lf(a,b){return Object.prototype.hasOwnProperty.call(a,b)}
;function Nf(a){var b=[];Qf(new Rf,a,b);return b.join("")}
function Rf(){}
function Qf(a,b,c){if(null==b)c.push("null");else{if("object"==typeof b){if(Array.isArray(b)){var d=b;b=d.length;c.push("[");for(var e="",f=0;f<b;f++)c.push(e),Qf(a,d[f],c),e=",";c.push("]");return}if(b instanceof String||b instanceof Number||b instanceof Boolean)b=b.valueOf();else{c.push("{");e="";for(d in b)Object.prototype.hasOwnProperty.call(b,d)&&(f=b[d],"function"!=typeof f&&(c.push(e),Sf(d,c),c.push(":"),Qf(a,f,c),e=","));c.push("}");return}}switch(typeof b){case "string":Sf(b,c);break;case "number":c.push(isFinite(b)&&
!isNaN(b)?String(b):"null");break;case "boolean":c.push(String(b));break;case "function":c.push("null");break;default:throw Error("Unknown type: "+typeof b);}}}
var Tf={'"':'\\"',"\\":"\\\\","/":"\\/","\b":"\\b","\f":"\\f","\n":"\\n","\r":"\\r","\t":"\\t","\v":"\\u000b"},Uf=/\uffff/.test("\uffff")?/[\\"\x00-\x1f\x7f-\uffff]/g:/[\\"\x00-\x1f\x7f-\xff]/g;function Sf(a,b){b.push('"',a.replace(Uf,function(c){var d=Tf[c];d||(d="\\u"+(c.charCodeAt(0)|65536).toString(16).slice(1),Tf[c]=d);return d}),'"')}
;function Vf(a){this.i=0;this.D=void 0;this.m=this.j=this.l=null;this.s=this.o=!1;if(a!=bb)try{var b=this;a.call(void 0,function(c){Wf(b,2,c)},function(c){Wf(b,3,c)})}catch(c){Wf(this,3,c)}}
function Xf(){this.next=this.context=this.onRejected=this.j=this.i=null;this.l=!1}
Xf.prototype.reset=function(){this.context=this.onRejected=this.j=this.i=null;this.l=!1};
var Yf=new ff(function(){return new Xf},function(a){a.reset()});
function Zf(a,b,c){var d=Yf.get();d.j=a;d.onRejected=b;d.context=c;return d}
function $f(a){return new Vf(function(b,c){c(a)})}
Vf.prototype.then=function(a,b,c){return ag(this,"function"===typeof a?a:null,"function"===typeof b?b:null,c)};
Vf.prototype.$goog_Thenable=!0;function bg(a,b){return ag(a,null,b)}
Vf.prototype.cancel=function(a){if(0==this.i){var b=new cg(a);nf(function(){dg(this,b)},this)}};
function dg(a,b){if(0==a.i)if(a.l){var c=a.l;if(c.j){for(var d=0,e=null,f=null,g=c.j;g&&(g.l||(d++,g.i==a&&(e=g),!(e&&1<d)));g=g.next)e||(f=g);e&&(0==c.i&&1==d?dg(c,b):(f?(d=f,d.next==c.m&&(c.m=d),d.next=d.next.next):eg(c),fg(c,e,3,b)))}a.l=null}else Wf(a,3,b)}
function gg(a,b){a.j||2!=a.i&&3!=a.i||hg(a);a.m?a.m.next=b:a.j=b;a.m=b}
function ag(a,b,c,d){var e=Zf(null,null,null);e.i=new Vf(function(f,g){e.j=b?function(h){try{var k=b.call(d,h);f(k)}catch(m){g(m)}}:f;
e.onRejected=c?function(h){try{var k=c.call(d,h);void 0===k&&h instanceof cg?g(h):f(k)}catch(m){g(m)}}:g});
e.i.l=a;gg(a,e);return e.i}
Vf.prototype.A=function(a){this.i=0;Wf(this,2,a)};
Vf.prototype.L=function(a){this.i=0;Wf(this,3,a)};
function Wf(a,b,c){if(0==a.i){a===c&&(b=3,c=new TypeError("Promise cannot resolve to itself"));a.i=1;a:{var d=c,e=a.A,f=a.L;if(d instanceof Vf){gg(d,Zf(e||bb,f||null,a));var g=!0}else{if(d)try{var h=!!d.$goog_Thenable}catch(m){h=!1}else h=!1;if(h)d.then(e,f,a),g=!0;else{if(Qa(d))try{var k=d.then;if("function"===typeof k){ig(d,k,e,f,a);g=!0;break a}}catch(m){f.call(a,m);g=!0;break a}g=!1}}}g||(a.D=c,a.i=b,a.l=null,hg(a),3!=b||c instanceof cg||jg(a,c))}}
function ig(a,b,c,d,e){function f(k){h||(h=!0,d.call(e,k))}
function g(k){h||(h=!0,c.call(e,k))}
var h=!1;try{b.call(a,g,f)}catch(k){f(k)}}
function hg(a){a.o||(a.o=!0,nf(a.v,a))}
function eg(a){var b=null;a.j&&(b=a.j,a.j=b.next,b.next=null);a.j||(a.m=null);return b}
Vf.prototype.v=function(){for(var a;a=eg(this);)fg(this,a,this.i,this.D);this.o=!1};
function fg(a,b,c,d){if(3==c&&b.onRejected&&!b.l)for(;a&&a.s;a=a.l)a.s=!1;if(b.i)b.i.l=null,kg(b,c,d);else try{b.l?b.j.call(b.context):kg(b,c,d)}catch(e){lg.call(null,e)}gf(Yf,b)}
function kg(a,b,c){2==b?a.j.call(a.context,c):a.onRejected&&a.onRejected.call(a.context,c)}
function jg(a,b){a.s=!0;nf(function(){a.s&&lg.call(null,b)})}
var lg=bd;function cg(a){$a.call(this,a)}
Ya(cg,$a);cg.prototype.name="cancel";function K(a){J.call(this);this.v=1;this.m=[];this.o=0;this.j=[];this.l={};this.A=!!a}
Ya(K,J);l=K.prototype;l.subscribe=function(a,b,c){var d=this.l[a];d||(d=this.l[a]=[]);var e=this.v;this.j[e]=a;this.j[e+1]=b;this.j[e+2]=c;this.v=e+3;d.push(e);return e};
function mg(a,b,c,d){if(b=a.l[b]){var e=a.j;(b=b.find(function(f){return e[f+1]==c&&e[f+2]==d}))&&a.ra(b)}}
l.ra=function(a){var b=this.j[a];if(b){var c=this.l[b];0!=this.o?(this.m.push(a),this.j[a+1]=function(){}):(c&&jb(c,a),delete this.j[a],delete this.j[a+1],delete this.j[a+2])}return!!b};
l.ha=function(a,b){var c=this.l[a];if(c){for(var d=Array(arguments.length-1),e=1,f=arguments.length;e<f;e++)d[e-1]=arguments[e];if(this.A)for(e=0;e<c.length;e++){var g=c[e];ng(this.j[g+1],this.j[g+2],d)}else{this.o++;try{for(e=0,f=c.length;e<f&&!this.i();e++)g=c[e],this.j[g+1].apply(this.j[g+2],d)}finally{if(this.o--,0<this.m.length&&0==this.o)for(;c=this.m.pop();)this.ra(c)}}return 0!=e}return!1};
function ng(a,b,c){nf(function(){a.apply(b,c)})}
l.clear=function(a){if(a){var b=this.l[a];b&&(b.forEach(this.ra,this),delete this.l[a])}else this.j.length=0,this.l={}};
l.I=function(){K.Y.I.call(this);this.clear();this.m.length=0};function og(a){this.i=a}
og.prototype.set=function(a,b){void 0===b?this.i.remove(a):this.i.set(a,Nf(b))};
og.prototype.get=function(a){try{var b=this.i.get(a)}catch(c){return}if(null!==b)try{return JSON.parse(b)}catch(c){throw"Storage: Invalid value was encountered";}};
og.prototype.remove=function(a){this.i.remove(a)};function pg(a){this.i=a}
Ya(pg,og);function qg(a){this.data=a}
function rg(a){return void 0===a||a instanceof qg?a:new qg(a)}
pg.prototype.set=function(a,b){pg.Y.set.call(this,a,rg(b))};
pg.prototype.j=function(a){a=pg.Y.get.call(this,a);if(void 0===a||a instanceof Object)return a;throw"Storage: Invalid value was encountered";};
pg.prototype.get=function(a){if(a=this.j(a)){if(a=a.data,void 0===a)throw"Storage: Invalid value was encountered";}else a=void 0;return a};function sg(a){this.i=a}
Ya(sg,pg);sg.prototype.set=function(a,b,c){if(b=rg(b)){if(c){if(c<Date.now()){sg.prototype.remove.call(this,a);return}b.expiration=c}b.creation=Date.now()}sg.Y.set.call(this,a,b)};
sg.prototype.j=function(a){var b=sg.Y.j.call(this,a);if(b){var c=b.creation,d=b.expiration;if(d&&d<Date.now()||c&&c>Date.now())sg.prototype.remove.call(this,a);else return b}};function tg(){}
;function ug(){}
Ya(ug,tg);ug.prototype[Symbol.iterator]=function(){return Ff(this.X(!0)).i()};
ug.prototype.clear=function(){var a=Array.from(this);a=q(a);for(var b=a.next();!b.done;b=a.next())this.remove(b.value)};function vg(a){this.i=a}
Ya(vg,ug);l=vg.prototype;l.isAvailable=function(){if(!this.i)return!1;try{return this.i.setItem("__sak","1"),this.i.removeItem("__sak"),!0}catch(a){return!1}};
l.set=function(a,b){try{this.i.setItem(a,b)}catch(c){if(0==this.i.length)throw"Storage mechanism: Storage disabled";throw"Storage mechanism: Quota exceeded";}};
l.get=function(a){a=this.i.getItem(a);if("string"!==typeof a&&null!==a)throw"Storage mechanism: Invalid value was encountered";return a};
l.remove=function(a){this.i.removeItem(a)};
l.X=function(a){var b=0,c=this.i,d=new Cf;d.next=function(){if(b>=c.length)return Df;var e=c.key(b++);if(a)return Ef(e);e=c.getItem(e);if("string"!==typeof e)throw"Storage mechanism: Invalid value was encountered";return Ef(e)};
return d};
l.clear=function(){this.i.clear()};
l.key=function(a){return this.i.key(a)};function wg(){var a=null;try{a=window.localStorage||null}catch(b){}this.i=a}
Ya(wg,vg);function xg(a,b){this.j=a;this.i=null;var c;if(c=sc)c=!(9<=Number(Fc));if(c){yg||(yg=new Jf);this.i=yg.get(a);this.i||(b?this.i=document.getElementById(b):(this.i=document.createElement("userdata"),this.i.addBehavior("#default#userData"),document.body.appendChild(this.i)),yg.set(a,this.i));try{this.i.load(this.j)}catch(d){this.i=null}}}
Ya(xg,ug);var zg={".":".2E","!":".21","~":".7E","*":".2A","'":".27","(":".28",")":".29","%":"."},yg=null;function Ag(a){return"_"+encodeURIComponent(a).replace(/[.!~*'()%]/g,function(b){return zg[b]})}
l=xg.prototype;l.isAvailable=function(){return!!this.i};
l.set=function(a,b){this.i.setAttribute(Ag(a),b);Bg(this)};
l.get=function(a){a=this.i.getAttribute(Ag(a));if("string"!==typeof a&&null!==a)throw"Storage mechanism: Invalid value was encountered";return a};
l.remove=function(a){this.i.removeAttribute(Ag(a));Bg(this)};
l.X=function(a){var b=0,c=this.i.XMLDocument.documentElement.attributes,d=new Cf;d.next=function(){if(b>=c.length)return Df;var e=c[b++];if(a)return Ef(decodeURIComponent(e.nodeName.replace(/\./g,"%")).slice(1));e=e.nodeValue;if("string"!==typeof e)throw"Storage mechanism: Invalid value was encountered";return Ef(e)};
return d};
l.clear=function(){for(var a=this.i.XMLDocument.documentElement,b=a.attributes.length;0<b;b--)a.removeAttribute(a.attributes[b-1].nodeName);Bg(this)};
function Bg(a){try{a.i.save(a.j)}catch(b){throw"Storage mechanism: Quota exceeded";}}
;function Cg(a,b){this.j=a;this.i=b+"::"}
Ya(Cg,ug);Cg.prototype.set=function(a,b){this.j.set(this.i+a,b)};
Cg.prototype.get=function(a){return this.j.get(this.i+a)};
Cg.prototype.remove=function(a){this.j.remove(this.i+a)};
Cg.prototype.X=function(a){var b=this.j[Symbol.iterator](),c=this,d=new Cf;d.next=function(){var e=b.next();if(e.done)return e;for(e=e.value;e.slice(0,c.i.length)!=c.i;){e=b.next();if(e.done)return e;e=e.value}return Ef(a?e.slice(c.i.length):c.j.get(e))};
return d};function Dg(a){I.call(this,a)}
r(Dg,I);Dg.prototype.getKey=function(){return jd(this,1)};
Dg.prototype.getValue=function(){return jd(this,2===md(this,Eg)?2:-1)};
Dg.prototype.setValue=function(a){return ld(this,2,Eg,a)};
var Eg=[2,3,4,5,6];function Fg(a){I.call(this,a)}
r(Fg,I);function Gg(a){I.call(this,a)}
r(Gg,I);function Hg(a){I.call(this,a,-1,Ig)}
r(Hg,I);Hg.prototype.getPlayerType=function(){return jd(this,36)};
Hg.prototype.setHomeGroupInfo=function(a){return D(this,81,a)};
var Ig=[9,66,24,32,86,100,101];function Jg(a){I.call(this,a,-1,Kg)}
r(Jg,I);var Kg=[15,26,28];function Lg(a){I.call(this,a)}
r(Lg,I);Lg.prototype.setToken=function(a){return C(this,2,a)};function Mg(a){I.call(this,a,-1,Ng)}
r(Mg,I);Mg.prototype.setSafetyMode=function(a){return C(this,5,a)};
var Ng=[12];function Og(a){I.call(this,a,-1,Pg)}
r(Og,I);var Pg=[12];function Qg(a){I.call(this,a,-1,Rg)}
r(Qg,I);function Sg(a){I.call(this,a)}
r(Sg,I);Sg.prototype.getKey=function(){return rd(this,1)};
Sg.prototype.getValue=function(){return rd(this,2)};
Sg.prototype.setValue=function(a){return C(this,2,a)};
var Rg=[4,5];function Tg(a){I.call(this,a)}
r(Tg,I);function Ug(a){I.call(this,a)}
r(Ug,I);var Vg=[2,3,4];function Wg(a){I.call(this,a)}
r(Wg,I);Wg.prototype.getMessage=function(){return rd(this,1)};function Xg(a){I.call(this,a)}
r(Xg,I);function Yg(a){I.call(this,a)}
r(Yg,I);function Zg(a){I.call(this,a,-1,$g)}
r(Zg,I);var $g=[10,17];function ah(a){I.call(this,a)}
r(ah,I);function bh(a){I.call(this,a)}
r(bh,I);bh.prototype.P=function(a){C(this,1,a)};function ch(a){I.call(this,a)}
r(ch,I);function dh(a){I.call(this,a)}
r(dh,I);function eh(a){I.call(this,a)}
r(eh,I);function fh(a,b){D(a,1,b)}
eh.prototype.P=function(a){C(this,2,a)};
function gh(a){I.call(this,a)}
r(gh,I);function hh(a,b){D(a,1,b)}
;function ih(a){I.call(this,a,-1,jh)}
r(ih,I);ih.prototype.P=function(a){C(this,1,a)};
function kh(a,b){D(a,2,b)}
var jh=[3];function lh(a){I.call(this,a)}
r(lh,I);lh.prototype.P=function(a){C(this,1,a)};function mh(a){I.call(this,a)}
r(mh,I);mh.prototype.P=function(a){C(this,1,a)};function nh(a){I.call(this,a)}
r(nh,I);nh.prototype.P=function(a){C(this,1,a)};function oh(a){I.call(this,a)}
r(oh,I);function ph(a){I.call(this,a)}
r(ph,I);function qh(a){I.call(this,a,-1,rh)}
r(qh,I);function sh(a,b){return C(a,1,b)}
qh.prototype.getPlayerType=function(){var a=jd(this,7);return null==a?0:a};
qh.prototype.setVideoId=function(a){return C(this,19,a)};
function th(a,b){return C(a,25,b)}
function uh(a,b){qd(a,68,vh,b)}
function vh(a){I.call(this,a)}
r(vh,I);vh.prototype.getId=function(){return rd(this,2)};
var rh=[83,68];function wh(a){I.call(this,a)}
r(wh,I);function xh(a){I.call(this,a)}
r(xh,I);function yh(a){I.call(this,a)}
r(yh,I);function zh(a){I.call(this,a,432)}
r(zh,I);
var Ah=[23,24,11,6,7,5,2,3,20,21,28,32,37,229,241,45,59,225,288,72,73,78,208,156,202,215,74,76,79,80,111,85,91,97,100,102,105,119,126,127,136,146,157,158,159,163,164,168,176,222,383,177,178,179,411,184,188,189,190,191,193,194,195,196,198,199,200,201,203,204,205,206,258,259,260,261,209,226,227,232,233,234,240,247,248,251,254,255,270,278,291,293,300,304,308,309,310,311,313,314,319,321,323,324,328,330,331,332,337,338,340,344,348,350,351,352,353,354,355,356,357,358,361,363,364,368,369,370,373,374,375,
378,380,381,388,389,403,412,429,413,414,415,416,417,418,430,423,424,425,426,427,431,117];var Bh={Nh:0,yh:1,Eh:2,Fh:4,Kh:8,Gh:16,Hh:32,Mh:64,Lh:128,Ah:256,Ch:512,Jh:1024,Bh:2048,Dh:4096,zh:8192,Ih:16384};function Ch(a){I.call(this,a)}
r(Ch,I);function Dh(a){I.call(this,a)}
r(Dh,I);Dh.prototype.setVideoId=function(a){return ld(this,1,Eh,a)};
Dh.prototype.getPlaylistId=function(){return jd(this,2===md(this,Eh)?2:-1)};
var Eh=[1,2];function Fh(a){I.call(this,a,-1,Gh)}
r(Fh,I);var Gh=[3];function Hh(a,b){1<b.length?a[b[0]]=b[1]:1===b.length&&Object.assign(a,b[0])}
;var Ih=y.window,Jh,Kh,Lh=(null==Ih?void 0:null==(Jh=Ih.yt)?void 0:Jh.config_)||(null==Ih?void 0:null==(Kh=Ih.ytcfg)?void 0:Kh.data_)||{};z("yt.config_",Lh);function Mh(){Hh(Lh,arguments)}
function L(a,b){return a in Lh?Lh[a]:b}
function Nh(){var a=Lh.EXPERIMENT_FLAGS;return a?a.web_disable_gel_stp_ecatcher_killswitch:void 0}
;function M(a){a=Oh(a);return"string"===typeof a&&"false"===a?!1:!!a}
function Ph(a,b){a=Oh(a);return void 0===a&&void 0!==b?b:Number(a||0)}
function Qh(){return L("EXPERIMENTS_TOKEN","")}
function Oh(a){var b=L("EXPERIMENTS_FORCED_FLAGS",{});return void 0!==b[a]?b[a]:L("EXPERIMENT_FLAGS",{})[a]}
function Rh(){var a=[],b=L("EXPERIMENTS_FORCED_FLAGS",{});for(c in b)a.push({key:c,value:String(b[c])});var c=L("EXPERIMENT_FLAGS",{});for(var d in c)d.startsWith("force_")&&void 0===b[d]&&a.push({key:d,value:String(c[d])});return a}
;var Sh=[];function Th(a){Sh.forEach(function(b){return b(a)})}
function Uh(a){return a&&window.yterr?function(){try{return a.apply(this,arguments)}catch(b){Vh(b)}}:a}
function Vh(a,b,c,d){var e=A("yt.logging.errors.log");e?e(a,"ERROR",b,c,d):(e=L("ERRORS",[]),e.push([a,"ERROR",b,c,d]),Mh("ERRORS",e));Th(a)}
function Wh(a,b,c,d){var e=A("yt.logging.errors.log");e?e(a,"WARNING",b,c,d):(e=L("ERRORS",[]),e.push([a,"WARNING",b,c,d]),Mh("ERRORS",e))}
;function Xh(){var a=Yh;A("yt.ads.biscotti.getId_")||z("yt.ads.biscotti.getId_",a)}
function Zh(a){z("yt.ads.biscotti.lastId_",a)}
;var $h=/^[\w.]*$/,ai={q:!0,search_query:!0};function bi(a,b){b=a.split(b);for(var c={},d=0,e=b.length;d<e;d++){var f=b[d].split("=");if(1==f.length&&f[0]||2==f.length)try{var g=ci(f[0]||""),h=ci(f[1]||"");g in c?Array.isArray(c[g])?kb(c[g],h):c[g]=[c[g],h]:c[g]=h}catch(u){var k=u,m=f[0],p=String(bi);k.args=[{key:m,value:f[1],query:a,method:di==p?"unchanged":p}];ai.hasOwnProperty(m)||Wh(k)}}return c}
var di=String(bi);function ei(a){var b=[];lb(a,function(c,d){var e=encodeURIComponent(String(d)),f;Array.isArray(c)?f=c:f=[c];eb(f,function(g){""==g?b.push(e):b.push(e+"="+encodeURIComponent(String(g)))})});
return b.join("&")}
function fi(a){"?"==a.charAt(0)&&(a=a.substr(1));return bi(a,"&")}
function gi(a){return-1!=a.indexOf("?")?(a=(a||"").split("#")[0],a=a.split("?",2),fi(1<a.length?a[1]:a[0])):{}}
function hi(a,b,c){var d=a.split("#",2);a=d[0];d=1<d.length?"#"+d[1]:"";var e=a.split("?",2);a=e[0];e=fi(e[1]||"");for(var f in b)!c&&null!==e&&f in e||(e[f]=b[f]);return gc(a,e)+d}
function ii(a){if(!b)var b=window.location.href;var c=bc(1,a),d=cc(a);c&&d?(a=a.match($b),b=b.match($b),a=a[3]==b[3]&&a[1]==b[1]&&a[4]==b[4]):a=d?cc(b)==d&&(Number(bc(4,b))||null)==(Number(bc(4,a))||null):!0;return a}
function ci(a){return a&&a.match($h)?a:decodeURIComponent(a.replace(/\+/g," "))}
;function ji(a){var b=ki;a=void 0===a?A("yt.ads.biscotti.lastId_")||"":a;var c=Object,d=c.assign,e={};e.dt=$d;e.flash="0";a:{try{var f=b.i.top.location.href}catch(ua){f=2;break a}f=f?f===b.j.location.href?0:1:2}e=(e.frm=f,e);try{e.u_tz=-(new Date).getTimezoneOffset();var g=void 0===g?Bd:g;try{var h=g.history.length}catch(ua){h=0}e.u_his=h;var k;e.u_h=null==(k=Bd.screen)?void 0:k.height;var m;e.u_w=null==(m=Bd.screen)?void 0:m.width;var p;e.u_ah=null==(p=Bd.screen)?void 0:p.availHeight;var u;e.u_aw=
null==(u=Bd.screen)?void 0:u.availWidth;var x;e.u_cd=null==(x=Bd.screen)?void 0:x.colorDepth}catch(ua){}h=b.i;try{var v=h.screenX;var E=h.screenY}catch(ua){}try{var G=h.outerWidth;var H=h.outerHeight}catch(ua){}try{var R=h.innerWidth;var N=h.innerHeight}catch(ua){}try{var S=h.screenLeft;var ja=h.screenTop}catch(ua){}try{R=h.innerWidth,N=h.innerHeight}catch(ua){}try{var O=h.screen.availWidth;var Ba=h.screen.availTop}catch(ua){}v=[S,ja,v,E,O,Ba,G,H,R,N];try{var Na=(b.i.top||window).document,va="CSS1Compat"==
Na.compatMode?Na.documentElement:Na.body;var F=(new Jd(va.clientWidth,va.clientHeight)).round()}catch(ua){F=new Jd(-12245933,-12245933)}Na=F;F={};var Ca=void 0===Ca?y:Ca;va=new df;Ca.SVGElement&&Ca.document.createElementNS&&va.set(0);E=Rd();E["allow-top-navigation-by-user-activation"]&&va.set(1);E["allow-popups-to-escape-sandbox"]&&va.set(2);Ca.crypto&&Ca.crypto.subtle&&va.set(3);Ca.TextDecoder&&Ca.TextEncoder&&va.set(4);Ca=ef(va);F.bc=Ca;F.bih=Na.height;F.biw=Na.width;F.brdim=v.join();b=b.j;b=(F.vis=
b.prerendering?3:{visible:1,hidden:2,prerender:3,preview:4,unloaded:5}[b.visibilityState||b.webkitVisibilityState||b.mozVisibilityState||""]||0,F.wgl=!!Bd.WebGLRenderingContext,F);c=d.call(c,e,b);c.ca_type="image";a&&(c.bid=a);return c}
var ki=new function(){var a=window.document;this.i=window;this.j=a};
z("yt.ads_.signals_.getAdSignalsString",function(a){return ei(ji(a))});Date.now();var li="XMLHttpRequest"in y?function(){return new XMLHttpRequest}:null;
function mi(){if(!li)return null;var a=li();return"open"in a?a:null}
function ni(a){switch(a&&"status"in a?a.status:-1){case 200:case 201:case 202:case 203:case 204:case 205:case 206:case 304:return!0;default:return!1}}
;function oi(a,b){"function"===typeof a&&(a=Uh(a));return window.setTimeout(a,b)}
function pi(a){window.clearTimeout(a)}
;var qi={Authorization:"AUTHORIZATION","X-Goog-EOM-Visitor-Id":"EOM_VISITOR_DATA","X-Goog-Visitor-Id":"SANDBOXED_VISITOR_ID","X-Youtube-Domain-Admin-State":"DOMAIN_ADMIN_STATE","X-Youtube-Chrome-Connected":"CHROME_CONNECTED_HEADER","X-YouTube-Client-Name":"INNERTUBE_CONTEXT_CLIENT_NAME","X-YouTube-Client-Version":"INNERTUBE_CONTEXT_CLIENT_VERSION","X-YouTube-Delegation-Context":"INNERTUBE_CONTEXT_SERIALIZED_DELEGATION_CONTEXT","X-YouTube-Device":"DEVICE","X-Youtube-Identity-Token":"ID_TOKEN","X-YouTube-Page-CL":"PAGE_CL",
"X-YouTube-Page-Label":"PAGE_BUILD_LABEL","X-YouTube-Variants-Checksum":"VARIANTS_CHECKSUM"},ri="app debugcss debugjs expflag force_ad_params force_ad_encrypted force_viral_ad_response_params forced_experiments innertube_snapshots innertube_goldens internalcountrycode internalipoverride absolute_experiments conditional_experiments sbb sr_bns_address".split(" ").concat(fa(be)),si=!1;
function ti(a,b){b=void 0===b?{}:b;var c=ii(a),d=M("web_ajax_ignore_global_headers_if_set"),e;for(e in qi){var f=L(qi[e]);M("enable_visitor_header_for_vss")&&"X-Goog-Visitor-Id"===e&&!f&&(f=L("VISITOR_DATA"));!f||!c&&cc(a)||d&&void 0!==b[e]||!M("enable_web_eom_visitor_data")&&"X-Goog-EOM-Visitor-Id"===e||(b[e]=f)}"X-Goog-EOM-Visitor-Id"in b&&"X-Goog-Visitor-Id"in b&&delete b["X-Goog-Visitor-Id"];if(c||!cc(a))b["X-YouTube-Utc-Offset"]=String(-(new Date).getTimezoneOffset());if(c||!cc(a)){try{var g=
(new Intl.DateTimeFormat).resolvedOptions().timeZone}catch(h){}g&&(b["X-YouTube-Time-Zone"]=g)}if(c||!cc(a))b["X-YouTube-Ad-Signals"]=ei(ji());return b}
function ui(a){var b=window.location.search,c=cc(a);M("debug_handle_relative_url_for_query_forward_killswitch")||c||!ii(a)||(c=document.location.hostname);var d=ac(bc(5,a));d=(c=c&&(c.endsWith("youtube.com")||c.endsWith("youtube-nocookie.com")))&&d&&d.startsWith("/api/");if(!c||d)return a;var e=fi(b),f={};eb(ri,function(g){e[g]&&(f[g]=e[g])});
return hi(a,f||{},!1)}
function vi(a,b){var c=b.format||"JSON";a=Ri(a,b);var d=Si(a,b),e=!1,f=Ti(a,function(k){if(!e){e=!0;h&&pi(h);var m=ni(k),p=null,u=400<=k.status&&500>k.status,x=500<=k.status&&600>k.status;if(m||u||x)p=Ui(a,c,k,b.convertToSafeHtml);if(m)a:if(k&&204==k.status)m=!0;else{switch(c){case "XML":m=0==parseInt(p&&p.return_code,10);break a;case "RAW":m=!0;break a}m=!!p}p=p||{};u=b.context||y;m?b.onSuccess&&b.onSuccess.call(u,k,p):b.onError&&b.onError.call(u,k,p);b.onFinish&&b.onFinish.call(u,k,p)}},b.method,
d,b.headers,b.responseType,b.withCredentials);
d=b.timeout||0;if(b.onTimeout&&0<d){var g=b.onTimeout;var h=oi(function(){e||(e=!0,f.abort(),pi(h),g.call(b.context||y,f))},d)}return f}
function Ri(a,b){b.includeDomain&&(a=document.location.protocol+"//"+document.location.hostname+(document.location.port?":"+document.location.port:"")+a);var c=L("XSRF_FIELD_NAME");if(b=b.urlParams)b[c]&&delete b[c],a=hi(a,b||{},!0);return a}
function Si(a,b){var c=L("XSRF_FIELD_NAME"),d=L("XSRF_TOKEN"),e=b.postBody||"",f=b.postParams,g=L("XSRF_FIELD_NAME"),h;b.headers&&(h=b.headers["Content-Type"]);b.excludeXsrf||cc(a)&&!b.withCredentials&&cc(a)!=document.location.hostname||"POST"!=b.method||h&&"application/x-www-form-urlencoded"!=h||b.postParams&&b.postParams[g]||(f||(f={}),f[c]=d);f&&"string"===typeof e&&(e=fi(e),xb(e,f),e=b.postBodyFormat&&"JSON"==b.postBodyFormat?JSON.stringify(e):fc(e));f=e||f&&!ob(f);!si&&f&&"POST"!=b.method&&(si=
!0,Vh(Error("AJAX request with postData should use POST")));return e}
function Ui(a,b,c,d){var e=null;switch(b){case "JSON":try{var f=c.responseText}catch(g){throw d=Error("Error reading responseText"),d.params=a,Wh(d),g;}a=c.getResponseHeader("Content-Type")||"";f&&0<=a.indexOf("json")&&(")]}'\n"===f.substring(0,5)&&(f=f.substring(5)),e=JSON.parse(f));break;case "XML":if(a=(a=c.responseXML)?Vi(a):null)e={},eb(a.getElementsByTagName("*"),function(g){e[g.tagName]=Wi(g)})}d&&Xi(e);
return e}
function Xi(a){if(Qa(a))for(var b in a){var c;(c="html_content"==b)||(c=b.length-5,c=0<=c&&b.indexOf("_html",c)==c);if(c){c=b;Cb("HTML that is escaped and sanitized server-side and passed through yt.net.ajax");var d=a[b],e=zb();d=e?e.createHTML(d):d;a[c]=new Tb(d)}else Xi(a[b])}}
function Vi(a){return a?(a=("responseXML"in a?a.responseXML:a).getElementsByTagName("root"))&&0<a.length?a[0]:null:null}
function Wi(a){var b="";eb(a.childNodes,function(c){b+=c.nodeValue});
return b}
function Ti(a,b,c,d,e,f,g){function h(){4==(k&&"readyState"in k?k.readyState:0)&&b&&Uh(b)(k)}
c=void 0===c?"GET":c;d=void 0===d?"":d;var k=mi();if(!k)return null;"onloadend"in k?k.addEventListener("loadend",h,!1):k.onreadystatechange=h;M("debug_forward_web_query_parameters")&&(a=ui(a));k.open(c,a,!0);f&&(k.responseType=f);g&&(k.withCredentials=!0);c="POST"==c&&(void 0===window.FormData||!(d instanceof FormData));if(e=ti(a,e))for(var m in e)k.setRequestHeader(m,e[m]),"content-type"==m.toLowerCase()&&(c=!1);c&&k.setRequestHeader("Content-Type","application/x-www-form-urlencoded");k.send(d);
return k}
;function Yi(a){var b=this;this.i=void 0;a.addEventListener("beforeinstallprompt",function(c){c.preventDefault();b.i=c})}
function Zi(){if(!y.matchMedia)return"WEB_DISPLAY_MODE_UNKNOWN";try{return y.matchMedia("(display-mode: standalone)").matches?"WEB_DISPLAY_MODE_STANDALONE":y.matchMedia("(display-mode: minimal-ui)").matches?"WEB_DISPLAY_MODE_MINIMAL_UI":y.matchMedia("(display-mode: fullscreen)").matches?"WEB_DISPLAY_MODE_FULLSCREEN":y.matchMedia("(display-mode: browser)").matches?"WEB_DISPLAY_MODE_BROWSER":"WEB_DISPLAY_MODE_UNKNOWN"}catch(a){return"WEB_DISPLAY_MODE_UNKNOWN"}}
;function $i(a,b,c,d,e){je.set(""+a,b,{Na:c,path:"/",domain:void 0===d?"youtube.com":d,secure:void 0===e?!1:e})}
function aj(a){return je.get(""+a,void 0)}
function bj(){if(!je.isEnabled())return!1;if(!je.isEmpty())return!0;je.set("TESTCOOKIESENABLED","1",{Na:60});if("1"!==je.get("TESTCOOKIESENABLED"))return!1;je.remove("TESTCOOKIESENABLED");return!0}
;var cj=A("ytglobal.prefsUserPrefsPrefs_")||{};z("ytglobal.prefsUserPrefsPrefs_",cj);function dj(){this.i=L("ALT_PREF_COOKIE_NAME","PREF");this.j=L("ALT_PREF_COOKIE_DOMAIN","youtube.com");var a=aj(this.i);if(a){a=decodeURIComponent(a).split("&");for(var b=0;b<a.length;b++){var c=a[b].split("="),d=c[0];(c=c[1])&&(cj[d]=c.toString())}}}
dj.prototype.get=function(a,b){ej(a);fj(a);a=void 0!==cj[a]?cj[a].toString():null;return null!=a?a:b?b:""};
dj.prototype.set=function(a,b){ej(a);fj(a);if(null==b)throw Error("ExpectedNotNull");cj[a]=b.toString()};
function gj(a){return!!((hj("f"+(Math.floor(a/31)+1))||0)&1<<a%31)}
dj.prototype.remove=function(a){ej(a);fj(a);delete cj[a]};
dj.prototype.clear=function(){for(var a in cj)delete cj[a]};
function fj(a){if(/^f([1-9][0-9]*)$/.test(a))throw Error("ExpectedRegexMatch: "+a);}
function ej(a){if(!/^\w+$/.test(a))throw Error("ExpectedRegexMismatch: "+a);}
function hj(a){a=void 0!==cj[a]?cj[a].toString():null;return null!=a&&/^[A-Fa-f0-9]+$/.test(a)?parseInt(a,16):null}
Ma(dj);var ij={bluetooth:"CONN_DISCO",cellular:"CONN_CELLULAR_UNKNOWN",ethernet:"CONN_WIFI",none:"CONN_NONE",wifi:"CONN_WIFI",wimax:"CONN_CELLULAR_4G",other:"CONN_UNKNOWN",unknown:"CONN_UNKNOWN","slow-2g":"CONN_CELLULAR_2G","2g":"CONN_CELLULAR_2G","3g":"CONN_CELLULAR_3G","4g":"CONN_CELLULAR_4G"},jj={CONN_DEFAULT:0,CONN_UNKNOWN:1,CONN_NONE:2,CONN_WIFI:3,CONN_CELLULAR_2G:4,CONN_CELLULAR_3G:5,CONN_CELLULAR_4G:6,CONN_CELLULAR_UNKNOWN:7,CONN_DISCO:8,CONN_CELLULAR_5G:9,CONN_WIFI_METERED:10,CONN_CELLULAR_5G_SA:11,
CONN_CELLULAR_5G_NSA:12,CONN_INVALID:31},kj={EFFECTIVE_CONNECTION_TYPE_UNKNOWN:0,EFFECTIVE_CONNECTION_TYPE_OFFLINE:1,EFFECTIVE_CONNECTION_TYPE_SLOW_2G:2,EFFECTIVE_CONNECTION_TYPE_2G:3,EFFECTIVE_CONNECTION_TYPE_3G:4,EFFECTIVE_CONNECTION_TYPE_4G:5},lj={"slow-2g":"EFFECTIVE_CONNECTION_TYPE_SLOW_2G","2g":"EFFECTIVE_CONNECTION_TYPE_2G","3g":"EFFECTIVE_CONNECTION_TYPE_3G","4g":"EFFECTIVE_CONNECTION_TYPE_4G"};function mj(){var a=y.navigator;return a?a.connection:void 0}
function nj(){var a=mj();if(a){var b=ij[a.type||"unknown"]||"CONN_UNKNOWN";a=ij[a.effectiveType||"unknown"]||"CONN_UNKNOWN";"CONN_CELLULAR_UNKNOWN"===b&&"CONN_UNKNOWN"!==a&&(b=a);if("CONN_UNKNOWN"!==b)return b;if("CONN_UNKNOWN"!==a)return a}}
function oj(){var a=mj();if(null!=a&&a.effectiveType)return lj.hasOwnProperty(a.effectiveType)?lj[a.effectiveType]:"EFFECTIVE_CONNECTION_TYPE_UNKNOWN"}
;function pj(){return"INNERTUBE_API_KEY"in Lh&&"INNERTUBE_API_VERSION"in Lh}
function qj(){return{innertubeApiKey:L("INNERTUBE_API_KEY"),innertubeApiVersion:L("INNERTUBE_API_VERSION"),Za:L("INNERTUBE_CONTEXT_CLIENT_CONFIG_INFO"),ub:L("INNERTUBE_CONTEXT_CLIENT_NAME","WEB"),Ub:L("INNERTUBE_CONTEXT_CLIENT_NAME",1),innertubeContextClientVersion:L("INNERTUBE_CONTEXT_CLIENT_VERSION"),wb:L("INNERTUBE_CONTEXT_HL"),vb:L("INNERTUBE_CONTEXT_GL"),Vb:L("INNERTUBE_HOST_OVERRIDE")||"",Xb:!!L("INNERTUBE_USE_THIRD_PARTY_AUTH",!1),Wb:!!L("INNERTUBE_OMIT_API_KEY_WHEN_AUTH_HEADER_IS_PRESENT",
!1),appInstallData:L("SERIALIZED_CLIENT_CONFIG_DATA")}}
function rj(a){var b={client:{hl:a.wb,gl:a.vb,clientName:a.ub,clientVersion:a.innertubeContextClientVersion,configInfo:a.Za}};navigator.userAgent&&(b.client.userAgent=String(navigator.userAgent));var c=y.devicePixelRatio;c&&1!=c&&(b.client.screenDensityFloat=String(c));c=Qh();""!==c&&(b.client.experimentsToken=c);c=Rh();0<c.length&&(b.request={internalExperimentFlags:c});sj(a,void 0,b);tj(void 0,b);uj(a,void 0,b);vj(void 0,b);L("DELEGATED_SESSION_ID")&&!M("pageid_as_header_web")&&(b.user={onBehalfOfUser:L("DELEGATED_SESSION_ID")});
a=Object;c=a.assign;for(var d=b.client,e={},f=q(Object.entries(fi(L("DEVICE","")))),g=f.next();!g.done;g=f.next()){var h=q(g.value);g=h.next().value;h=h.next().value;"cbrand"===g?e.deviceMake=h:"cmodel"===g?e.deviceModel=h:"cbr"===g?e.browserName=h:"cbrver"===g?e.browserVersion=h:"cos"===g?e.osName=h:"cosver"===g?e.osVersion=h:"cplatform"===g&&(e.platform=h)}b.client=c.call(a,d,e);return b}
function wj(a){var b=new Og,c=new Hg;C(c,1,a.wb);C(c,2,a.vb);C(c,16,a.Ub);C(c,17,a.innertubeContextClientVersion);if(a.Za){var d=a.Za,e=new Fg;d.coldConfigData&&C(e,1,d.coldConfigData);d.appInstallData&&C(e,6,d.appInstallData);d.coldHashData&&C(e,3,d.coldHashData);d.hotHashData&&C(e,5,d.hotHashData);D(c,62,e)}(d=y.devicePixelRatio)&&1!=d&&C(c,65,d);d=Qh();""!==d&&C(c,54,d);d=Rh();if(0<d.length){e=new Jg;for(var f=0;f<d.length;f++){var g=new Dg;C(g,1,d[f].key);g.setValue(d[f].value);qd(e,15,Dg,g)}D(b,
5,e)}sj(a,c);tj(c);uj(a,c);vj(c);L("DELEGATED_SESSION_ID")&&!M("pageid_as_header_web")&&(a=new Mg,C(a,3,L("DELEGATED_SESSION_ID")));a=q(Object.entries(fi(L("DEVICE",""))));for(d=a.next();!d.done;d=a.next())e=q(d.value),d=e.next().value,e=e.next().value,"cbrand"===d?C(c,12,e):"cmodel"===d?C(c,13,e):"cbr"===d?C(c,87,e):"cbrver"===d?C(c,88,e):"cos"===d?C(c,18,e):"cosver"===d?C(c,19,e):"cplatform"===d&&C(c,42,e);D(b,1,c);return b}
function sj(a,b,c){a=a.ub;if("WEB"===a||"MWEB"===a||1===a||2===a)if(b){c=nd(b,Gg,96)||new Gg;var d=Zi();d=Object.keys(cf).indexOf(d);d=-1===d?null:d;null!==d&&C(c,3,d);D(b,96,c)}else c&&(c.client.mainAppWebInfo=null!=(d=c.client.mainAppWebInfo)?d:{},c.client.mainAppWebInfo.webDisplayMode=Zi())}
function tj(a,b){var c;if(M("web_log_memory_total_kbytes")&&(null==(c=y.navigator)?0:c.deviceMemory)){var d;c=null==(d=y.navigator)?void 0:d.deviceMemory;a?C(a,95,1E6*c):b&&(b.client.memoryTotalKbytes=""+1E6*c)}}
function uj(a,b,c){if(a.appInstallData)if(b){var d;c=null!=(d=nd(b,Fg,62))?d:new Fg;C(c,6,a.appInstallData);D(b,62,c)}else c&&(c.client.configInfo=c.client.configInfo||{},c.client.configInfo.appInstallData=a.appInstallData)}
function vj(a,b){var c=nj();c&&(a?C(a,61,jj[c]):b&&(b.client.connectionType=c));M("web_log_effective_connection_type")&&(c=oj())&&(a?C(a,94,kj[c]):b&&(b.client.effectiveConnectionType=c))}
function xj(a,b,c){c=void 0===c?{}:c;var d={};M("enable_web_eom_visitor_data")&&L("EOM_VISITOR_DATA")?d={"X-Goog-EOM-Visitor-Id":L("EOM_VISITOR_DATA")}:d={"X-Goog-Visitor-Id":c.visitorData||L("VISITOR_DATA","")};if(b&&b.includes("www.youtube-nocookie.com"))return d;(b=c.ep||L("AUTHORIZATION"))||(a?b="Bearer "+A("gapi.auth.getToken")().cp:b=ne([]));b&&(d.Authorization=b,d["X-Goog-AuthUser"]=L("SESSION_INDEX",0),M("pageid_as_header_web")&&(d["X-Goog-PageId"]=L("DELEGATED_SESSION_ID")));return d}
;function yj(a){a=Object.assign({},a);delete a.Authorization;var b=ne();if(b){var c=new wf;c.update(L("INNERTUBE_API_KEY"));c.update(b);a.hash=Lc(c.digest(),3)}return a}
;function zj(a){var b=new wg;(b=b.isAvailable()?a?new Cg(b,a):b:null)||(a=new xg(a||"UserDataSharedStore"),b=a.isAvailable()?a:null);this.i=(a=b)?new sg(a):null;this.j=document.domain||window.location.hostname}
zj.prototype.set=function(a,b,c,d){c=c||31104E3;this.remove(a);if(this.i)try{this.i.set(a,b,Date.now()+1E3*c);return}catch(f){}var e="";if(d)try{e=escape(Nf(b))}catch(f){return}else e=escape(b);$i(a,e,c,this.j)};
zj.prototype.get=function(a,b){var c=void 0,d=!this.i;if(!d)try{c=this.i.get(a)}catch(e){d=!0}if(d&&(c=aj(a))&&(c=unescape(c),b))try{c=JSON.parse(c)}catch(e){this.remove(a),c=void 0}return c};
zj.prototype.remove=function(a){this.i&&this.i.remove(a);var b=this.j;je.remove(""+a,"/",void 0===b?"youtube.com":b)};var Aj=window,P=Aj.ytcsi&&Aj.ytcsi.now?Aj.ytcsi.now:Aj.performance&&Aj.performance.timing&&Aj.performance.now&&Aj.performance.timing.navigationStart?function(){return Aj.performance.timing.navigationStart+Aj.performance.now()}:function(){return(new Date).getTime()};var Bj;function Cj(){Bj||(Bj=new zj("yt.innertube"));return Bj}
function Dj(a,b,c,d){if(d)return null;d=Cj().get("nextId",!0)||1;var e=Cj().get("requests",!0)||{};e[d]={method:a,request:b,authState:yj(c),requestTime:Math.round(P())};Cj().set("nextId",d+1,86400,!0);Cj().set("requests",e,86400,!0);return d}
function Ej(a){var b=Cj().get("requests",!0)||{};delete b[a];Cj().set("requests",b,86400,!0)}
function Fj(a){var b=Cj().get("requests",!0);if(b){for(var c in b){var d=b[c];if(!(6E4>Math.round(P())-d.requestTime)){var e=d.authState,f=yj(xj(!1));tb(e,f)&&(e=d.request,"requestTimeMs"in e&&(e.requestTimeMs=Math.round(P())),Gj(a,d.method,e,{}));delete b[c]}}Cj().set("requests",b,86400,!0)}}
;function Hj(){}
function Ij(a,b){return Jj(a,0,b)}
Hj.prototype.M=function(a,b){return Jj(a,1,b)};
function Kj(a,b){Jj(a,2,b)}
function Lj(a){var b=A("yt.scheduler.instance.addImmediateJob");b?b(a):a()}
;function Mj(){Hj.apply(this,arguments)}
r(Mj,Hj);function Nj(){Mj.i||(Mj.i=new Mj);return Mj.i}
function Jj(a,b,c){void 0!==c&&Number.isNaN(Number(c))&&(c=void 0);var d=A("yt.scheduler.instance.addJob");return d?d(a,b,c):void 0===c?(a(),NaN):oi(a,c||0)}
Mj.prototype.S=function(a){if(void 0===a||!Number.isNaN(Number(a))){var b=A("yt.scheduler.instance.cancelJob");b?b(a):pi(a)}};
Mj.prototype.start=function(){var a=A("yt.scheduler.instance.start");a&&a()};
Mj.prototype.pause=function(){var a=A("yt.scheduler.instance.pause");a&&a()};var af=Nj();var Oj=Gc||Hc;function Pj(a){var b=Qb();return b?0<=b.toLowerCase().indexOf(a):!1}
;var Qj=function(){var a;return function(){a||(a=new zj("ytidb"));return a}}();
function Rj(){var a;return null==(a=Qj())?void 0:a.get("LAST_RESULT_ENTRY_KEY",!0)}
;var Sj=[],Tj,Uj=!1;function Vj(){var a={};for(Tj=new Wj(void 0===a.handleError?Xj:a.handleError,void 0===a.logEvent?Yj:a.logEvent);0<Sj.length;)switch(a=Sj.shift(),a.type){case "ERROR":Tj.handleError(a.payload);break;case "EVENT":Tj.logEvent(a.eventType,a.payload)}}
function Zj(a){Uj||(Tj?Tj.handleError(a):(Sj.push({type:"ERROR",payload:a}),10<Sj.length&&Sj.shift()))}
function ak(a,b){Uj||(Tj?Tj.logEvent(a,b):(Sj.push({type:"EVENT",eventType:a,payload:b}),10<Sj.length&&Sj.shift()))}
;function Q(a){var b=Ja.apply(1,arguments);var c=Error.call(this,a);this.message=c.message;"stack"in c&&(this.stack=c.stack);this.args=[].concat(fa(b))}
r(Q,Error);function bk(){try{return ck(),!0}catch(a){return!1}}
function ck(a){if(void 0!==L("DATASYNC_ID"))return L("DATASYNC_ID");throw new Q("Datasync ID not set",void 0===a?"unknown":a);}
;function dk(a){if(0<=a.indexOf(":"))throw Error("Database name cannot contain ':'");}
function ek(a){return a.substr(0,a.indexOf(":"))||a}
;var fk={},gk=(fk.AUTH_INVALID="No user identifier specified.",fk.EXPLICIT_ABORT="Transaction was explicitly aborted.",fk.IDB_NOT_SUPPORTED="IndexedDB is not supported.",fk.MISSING_INDEX="Index not created.",fk.MISSING_OBJECT_STORES="Object stores not created.",fk.DB_DELETED_BY_MISSING_OBJECT_STORES="Database is deleted because expected object stores were not created.",fk.DB_REOPENED_BY_MISSING_OBJECT_STORES="Database is reopened because expected object stores were not created.",fk.UNKNOWN_ABORT="Transaction was aborted for unknown reasons.",
fk.QUOTA_EXCEEDED="The current transaction exceeded its quota limitations.",fk.QUOTA_MAYBE_EXCEEDED="The current transaction may have failed because of exceeding quota limitations.",fk.EXECUTE_TRANSACTION_ON_CLOSED_DB="Can't start a transaction on a closed database",fk.INCOMPATIBLE_DB_VERSION="The binary is incompatible with the database version",fk),hk={},ik=(hk.AUTH_INVALID="ERROR",hk.EXECUTE_TRANSACTION_ON_CLOSED_DB="WARNING",hk.EXPLICIT_ABORT="IGNORED",hk.IDB_NOT_SUPPORTED="ERROR",hk.MISSING_INDEX=
"WARNING",hk.MISSING_OBJECT_STORES="ERROR",hk.DB_DELETED_BY_MISSING_OBJECT_STORES="WARNING",hk.DB_REOPENED_BY_MISSING_OBJECT_STORES="WARNING",hk.QUOTA_EXCEEDED="WARNING",hk.QUOTA_MAYBE_EXCEEDED="WARNING",hk.UNKNOWN_ABORT="WARNING",hk.INCOMPATIBLE_DB_VERSION="WARNING",hk),jk={},kk=(jk.AUTH_INVALID=!1,jk.EXECUTE_TRANSACTION_ON_CLOSED_DB=!1,jk.EXPLICIT_ABORT=!1,jk.IDB_NOT_SUPPORTED=!1,jk.MISSING_INDEX=!1,jk.MISSING_OBJECT_STORES=!1,jk.DB_DELETED_BY_MISSING_OBJECT_STORES=!1,jk.DB_REOPENED_BY_MISSING_OBJECT_STORES=
!1,jk.QUOTA_EXCEEDED=!1,jk.QUOTA_MAYBE_EXCEEDED=!0,jk.UNKNOWN_ABORT=!0,jk.INCOMPATIBLE_DB_VERSION=!1,jk);function lk(a,b,c,d,e){b=void 0===b?{}:b;c=void 0===c?gk[a]:c;d=void 0===d?ik[a]:d;e=void 0===e?kk[a]:e;Q.call(this,c,Object.assign({},{name:"YtIdbKnownError",isSw:void 0===self.document,isIframe:self!==self.top,type:a},b));this.type=a;this.message=c;this.level=d;this.i=e;Object.setPrototypeOf(this,lk.prototype)}
r(lk,Q);function mk(a,b){lk.call(this,"MISSING_OBJECT_STORES",{expectedObjectStores:b,foundObjectStores:a},gk.MISSING_OBJECT_STORES);Object.setPrototypeOf(this,mk.prototype)}
r(mk,lk);function nk(a,b){var c=Error.call(this);this.message=c.message;"stack"in c&&(this.stack=c.stack);this.index=a;this.objectStore=b;Object.setPrototypeOf(this,nk.prototype)}
r(nk,Error);var ok=["The database connection is closing","Can't start a transaction on a closed database","A mutation operation was attempted on a database that did not allow mutations"];
function pk(a,b,c,d){b=ek(b);var e=a instanceof Error?a:Error("Unexpected error: "+a);if(e instanceof lk)return e;a={objectStoreNames:c,dbName:b,dbVersion:d};if("QuotaExceededError"===e.name)return new lk("QUOTA_EXCEEDED",a);if(Ic&&"UnknownError"===e.name)return new lk("QUOTA_MAYBE_EXCEEDED",a);if(e instanceof nk)return new lk("MISSING_INDEX",Object.assign({},a,{objectStore:e.objectStore,index:e.index}));if("InvalidStateError"===e.name&&ok.some(function(f){return e.message.includes(f)}))return new lk("EXECUTE_TRANSACTION_ON_CLOSED_DB",
a);
if("AbortError"===e.name)return new lk("UNKNOWN_ABORT",a,e.message);e.args=[Object.assign({},a,{name:"IdbError",Ab:e.name})];e.level="WARNING";return e}
function qk(a,b,c){var d=Rj();return new lk("IDB_NOT_SUPPORTED",{context:{caller:a,publicName:b,version:c,hasSucceededOnce:null==d?void 0:d.hasSucceededOnce}})}
;function rk(a){if(!a)throw Error();throw a;}
function sk(a){return a}
function tk(a){this.i=a}
function uk(a){function b(e){if("PENDING"===d.state.status){d.state={status:"REJECTED",reason:e};e=q(d.onRejected);for(var f=e.next();!f.done;f=e.next())f=f.value,f()}}
function c(e){if("PENDING"===d.state.status){d.state={status:"FULFILLED",value:e};e=q(d.i);for(var f=e.next();!f.done;f=e.next())f=f.value,f()}}
var d=this;this.state={status:"PENDING"};this.i=[];this.onRejected=[];a=a.i;try{a(c,b)}catch(e){b(e)}}
uk.all=function(a){return new uk(new tk(function(b,c){var d=[],e=a.length;0===e&&b(d);for(var f={ka:0};f.ka<a.length;f={ka:f.ka},++f.ka)vk(uk.resolve(a[f.ka]).then(function(g){return function(h){d[g.ka]=h;e--;0===e&&b(d)}}(f)),function(g){c(g)})}))};
uk.resolve=function(a){return new uk(new tk(function(b,c){a instanceof uk?a.then(b,c):b(a)}))};
uk.reject=function(a){return new uk(new tk(function(b,c){c(a)}))};
uk.prototype.then=function(a,b){var c=this,d=null!=a?a:sk,e=null!=b?b:rk;return new uk(new tk(function(f,g){"PENDING"===c.state.status?(c.i.push(function(){wk(c,c,d,f,g)}),c.onRejected.push(function(){xk(c,c,e,f,g)})):"FULFILLED"===c.state.status?wk(c,c,d,f,g):"REJECTED"===c.state.status&&xk(c,c,e,f,g)}))};
function vk(a,b){a.then(void 0,b)}
function wk(a,b,c,d,e){try{if("FULFILLED"!==a.state.status)throw Error("calling handleResolve before the promise is fulfilled.");var f=c(a.state.value);f instanceof uk?yk(a,b,f,d,e):d(f)}catch(g){e(g)}}
function xk(a,b,c,d,e){try{if("REJECTED"!==a.state.status)throw Error("calling handleReject before the promise is rejected.");var f=c(a.state.reason);f instanceof uk?yk(a,b,f,d,e):d(f)}catch(g){e(g)}}
function yk(a,b,c,d,e){b===c?e(new TypeError("Circular promise chain detected.")):c.then(function(f){f instanceof uk?yk(a,b,f,d,e):d(f)},function(f){e(f)})}
;function zk(a,b,c){function d(){c(a.error);f()}
function e(){b(a.result);f()}
function f(){try{a.removeEventListener("success",e),a.removeEventListener("error",d)}catch(g){}}
a.addEventListener("success",e);a.addEventListener("error",d)}
function Ak(a){return new Promise(function(b,c){zk(a,b,c)})}
function Bk(a){return new uk(new tk(function(b,c){zk(a,b,c)}))}
;function Ck(a,b){return new uk(new tk(function(c,d){function e(){var f=a?b(a):null;f?f.then(function(g){a=g;e()},d):c()}
e()}))}
;function Dk(a,b){this.i=a;this.options=b;this.transactionCount=0;this.l=Math.round(P());this.j=!1}
l=Dk.prototype;l.add=function(a,b,c){return Ek(this,[a],{mode:"readwrite",O:!0},function(d){return d.objectStore(a).add(b,c)})};
l.clear=function(a){return Ek(this,[a],{mode:"readwrite",O:!0},function(b){return b.objectStore(a).clear()})};
l.close=function(){this.i.close();var a;(null==(a=this.options)?0:a.closed)&&this.options.closed()};
l.count=function(a,b){return Ek(this,[a],{mode:"readonly",O:!0},function(c){return c.objectStore(a).count(b)})};
function Fk(a,b,c){a=a.i.createObjectStore(b,c);return new Gk(a)}
l.delete=function(a,b){return Ek(this,[a],{mode:"readwrite",O:!0},function(c){return c.objectStore(a).delete(b)})};
l.get=function(a,b){return Ek(this,[a],{mode:"readonly",O:!0},function(c){return c.objectStore(a).get(b)})};
function Hk(a,b){return Ek(a,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(c){c=c.objectStore("LogsRequestsStore");return Bk(c.i.put(b,void 0))})}
l.objectStoreNames=function(){return Array.from(this.i.objectStoreNames)};
function Ek(a,b,c,d){var e,f,g,h,k,m,p,u,x,v,E,G;return w(function(H){switch(H.i){case 1:var R={mode:"readonly",O:!1,tag:"IDB_TRANSACTION_TAG_UNKNOWN"};"string"===typeof c?R.mode=c:Object.assign(R,c);e=R;a.transactionCount++;f=e.O?3:1;g=0;case 2:if(h){H.u(3);break}g++;k=Math.round(P());wa(H,4);m=a.i.transaction(b,e.mode);R=new Ik(m);R=Jk(R,d);return t(H,R,6);case 6:return p=H.j,u=Math.round(P()),Kk(a,k,u,g,void 0,b.join(),e),H.return(p);case 4:x=ya(H);v=Math.round(P());E=pk(x,a.i.name,b.join(),a.i.version);
if((G=E instanceof lk&&!E.i)||g>=f)Kk(a,k,v,g,E,b.join(),e),h=E;H.u(2);break;case 3:return H.return(Promise.reject(h))}})}
function Kk(a,b,c,d,e,f,g){b=c-b;e?(e instanceof lk&&("QUOTA_EXCEEDED"===e.type||"QUOTA_MAYBE_EXCEEDED"===e.type)&&ak("QUOTA_EXCEEDED",{dbName:ek(a.i.name),objectStoreNames:f,transactionCount:a.transactionCount,transactionMode:g.mode}),e instanceof lk&&"UNKNOWN_ABORT"===e.type&&(c-=a.l,0>c&&c>=Math.pow(2,31)&&(c=0),ak("TRANSACTION_UNEXPECTEDLY_ABORTED",{objectStoreNames:f,transactionDuration:b,transactionCount:a.transactionCount,dbDuration:c}),a.j=!0),Lk(a,!1,d,f,b,g.tag),Zj(e)):Lk(a,!0,d,f,b,g.tag)}
function Lk(a,b,c,d,e,f){ak("TRANSACTION_ENDED",{objectStoreNames:d,connectionHasUnknownAbortedTransaction:a.j,duration:e,isSuccessful:b,tryCount:c,tag:void 0===f?"IDB_TRANSACTION_TAG_UNKNOWN":f})}
l.getName=function(){return this.i.name};
function Gk(a){this.i=a}
l=Gk.prototype;l.add=function(a,b){return Bk(this.i.add(a,b))};
l.autoIncrement=function(){return this.i.autoIncrement};
l.clear=function(){return Bk(this.i.clear()).then(function(){})};
l.count=function(a){return Bk(this.i.count(a))};
function Mk(a,b){return Nk(a,{query:b},function(c){return c.delete().then(function(){return c.continue()})}).then(function(){})}
l.delete=function(a){return a instanceof IDBKeyRange?Mk(this,a):Bk(this.i.delete(a))};
l.get=function(a){return Bk(this.i.get(a))};
l.index=function(a){try{return new Ok(this.i.index(a))}catch(b){if(b instanceof Error&&"NotFoundError"===b.name)throw new nk(a,this.i.name);throw b;}};
l.getName=function(){return this.i.name};
l.keyPath=function(){return this.i.keyPath};
function Nk(a,b,c){a=a.i.openCursor(b.query,b.direction);return Pk(a).then(function(d){return Ck(d,c)})}
function Ik(a){var b=this;this.i=a;this.l=new Map;this.j=!1;this.done=new Promise(function(c,d){b.i.addEventListener("complete",function(){c()});
b.i.addEventListener("error",function(e){e.currentTarget===e.target&&d(b.i.error)});
b.i.addEventListener("abort",function(){var e=b.i.error;if(e)d(e);else if(!b.j){e=lk;for(var f=b.i.objectStoreNames,g=[],h=0;h<f.length;h++){var k=f.item(h);if(null===k)throw Error("Invariant: item in DOMStringList is null");g.push(k)}e=new e("UNKNOWN_ABORT",{objectStoreNames:g.join(),dbName:b.i.db.name,mode:b.i.mode});d(e)}})})}
function Jk(a,b){var c=new Promise(function(d,e){try{vk(b(a).then(function(f){d(f)}),e)}catch(f){e(f),a.abort()}});
return Promise.all([c,a.done]).then(function(d){return q(d).next().value})}
Ik.prototype.abort=function(){this.i.abort();this.j=!0;throw new lk("EXPLICIT_ABORT");};
Ik.prototype.objectStore=function(a){a=this.i.objectStore(a);var b=this.l.get(a);b||(b=new Gk(a),this.l.set(a,b));return b};
function Ok(a){this.i=a}
l=Ok.prototype;l.count=function(a){return Bk(this.i.count(a))};
l.delete=function(a){return Qk(this,{query:a},function(b){return b.delete().then(function(){return b.continue()})})};
l.get=function(a){return Bk(this.i.get(a))};
l.getKey=function(a){return Bk(this.i.getKey(a))};
l.keyPath=function(){return this.i.keyPath};
l.unique=function(){return this.i.unique};
function Qk(a,b,c){a=a.i.openCursor(void 0===b.query?null:b.query,void 0===b.direction?"next":b.direction);return Pk(a).then(function(d){return Ck(d,c)})}
function Rk(a,b){this.request=a;this.cursor=b}
function Pk(a){return Bk(a).then(function(b){return b?new Rk(a,b):null})}
l=Rk.prototype;l.advance=function(a){this.cursor.advance(a);return Pk(this.request)};
l.continue=function(a){this.cursor.continue(a);return Pk(this.request)};
l.delete=function(){return Bk(this.cursor.delete()).then(function(){})};
l.getKey=function(){return this.cursor.key};
l.getValue=function(){return this.cursor.value};
l.update=function(a){return Bk(this.cursor.update(a))};function Sk(a,b,c){return new Promise(function(d,e){function f(){x||(x=new Dk(g.result,{closed:u}));return x}
var g=void 0!==b?self.indexedDB.open(a,b):self.indexedDB.open(a);var h=c.blocked,k=c.blocking,m=c.yc,p=c.upgrade,u=c.closed,x;g.addEventListener("upgradeneeded",function(v){try{if(null===v.newVersion)throw Error("Invariant: newVersion on IDbVersionChangeEvent is null");if(null===g.transaction)throw Error("Invariant: transaction on IDbOpenDbRequest is null");v.dataLoss&&"none"!==v.dataLoss&&ak("IDB_DATA_CORRUPTED",{reason:v.dataLossMessage||"unknown reason",dbName:ek(a)});var E=f(),G=new Ik(g.transaction);
p&&p(E,function(H){return v.oldVersion<H&&v.newVersion>=H},G);
G.done.catch(function(H){e(H)})}catch(H){e(H)}});
g.addEventListener("success",function(){var v=g.result;k&&v.addEventListener("versionchange",function(){k(f())});
v.addEventListener("close",function(){ak("IDB_UNEXPECTEDLY_CLOSED",{dbName:ek(a),dbVersion:v.version});m&&m()});
d(f())});
g.addEventListener("error",function(){e(g.error)});
h&&g.addEventListener("blocked",function(){h()})})}
function Tk(a,b,c){c=void 0===c?{}:c;return Sk(a,b,c)}
function Uk(a,b){b=void 0===b?{}:b;var c,d,e,f;return w(function(g){if(1==g.i)return wa(g,2),c=self.indexedDB.deleteDatabase(a),d=b,(e=d.blocked)&&c.addEventListener("blocked",function(){e()}),t(g,Ak(c),4);
if(2!=g.i)return xa(g,0);f=ya(g);throw pk(f,a,"",-1);})}
;function Vk(a){return new Promise(function(b){Kj(function(){b()},a)})}
function Wk(a,b){this.name=a;this.options=b;this.m=!0;this.o=this.s=0;this.j=500}
Wk.prototype.l=function(a,b,c){c=void 0===c?{}:c;return Tk(a,b,c)};
Wk.prototype.delete=function(a){a=void 0===a?{}:a;return Uk(this.name,a)};
function Xk(a,b){return new lk("INCOMPATIBLE_DB_VERSION",{dbName:a.name,oldVersion:a.options.version,newVersion:b})}
function Yk(a,b){if(!b)throw qk("openWithToken",ek(a.name));return Zk(a)}
function Zk(a){function b(){var f,g,h,k,m,p,u,x,v,E;return w(function(G){switch(G.i){case 1:return g=null!=(f=Error().stack)?f:"",wa(G,2),t(G,a.l(a.name,a.options.version,d),4);case 4:h=G.j;for(var H=a.options,R=[],N=q(Object.keys(H.va)),S=N.next();!S.done;S=N.next()){S=S.value;var ja=H.va[S],O=void 0===ja.hc?Number.MAX_VALUE:ja.hc;!(h.i.version>=ja.Ua)||h.i.version>=O||h.i.objectStoreNames.contains(S)||R.push(S)}k=R;if(0===k.length){G.u(5);break}m=Object.keys(a.options.va);p=h.objectStoreNames();
if(a.o<Ph("ytidb_reopen_db_retries",0))return a.o++,h.close(),Zj(new lk("DB_REOPENED_BY_MISSING_OBJECT_STORES",{dbName:a.name,expectedObjectStores:m,foundObjectStores:p})),G.return(b());if(!(a.s<Ph("ytidb_remake_db_retries",1))){G.u(6);break}a.s++;if(!M("ytidb_remake_db_enable_backoff_delay")){G.u(7);break}return t(G,Vk(a.j),8);case 8:a.j*=2;case 7:return t(G,a.delete(),9);case 9:return Zj(new lk("DB_DELETED_BY_MISSING_OBJECT_STORES",{dbName:a.name,expectedObjectStores:m,foundObjectStores:p})),G.return(b());
case 6:throw new mk(p,m);case 5:return G.return(h);case 2:u=ya(G);if(u instanceof DOMException?"VersionError"!==u.name:"DOMError"in self&&u instanceof DOMError?"VersionError"!==u.name:!(u instanceof Object&&"message"in u)||"An attempt was made to open a database using a lower version than the existing version."!==u.message){G.u(10);break}return t(G,a.l(a.name,void 0,Object.assign({},d,{upgrade:void 0})),11);case 11:x=G.j;v=x.i.version;if(void 0!==a.options.version&&v>a.options.version+1)throw x.close(),
a.m=!1,Xk(a,v);return G.return(x);case 10:throw c(),u instanceof Error&&!M("ytidb_async_stack_killswitch")&&(u.stack=u.stack+"\n"+g.substring(g.indexOf("\n")+1)),pk(u,a.name,"",null!=(E=a.options.version)?E:-1);}})}
function c(){a.i===e&&(a.i=void 0)}
if(!a.m)throw Xk(a);if(a.i)return a.i;var d={blocking:function(f){f.close()},
closed:c,yc:c,upgrade:a.options.upgrade};var e=b();a.i=e;return a.i}
;var $k=new Wk("YtIdbMeta",{va:{databases:{Ua:1}},upgrade:function(a,b){b(1)&&Fk(a,"databases",{keyPath:"actualName"})}});
function al(a,b){var c;return w(function(d){if(1==d.i)return t(d,Yk($k,b),2);c=d.j;return d.return(Ek(c,["databases"],{O:!0,mode:"readwrite"},function(e){var f=e.objectStore("databases");return f.get(a.actualName).then(function(g){if(g?a.actualName!==g.actualName||a.publicName!==g.publicName||a.userIdentifier!==g.userIdentifier:1)return Bk(f.i.put(a,void 0)).then(function(){})})}))})}
function bl(a,b){var c;return w(function(d){if(1==d.i)return a?t(d,Yk($k,b),2):d.return();c=d.j;return d.return(c.delete("databases",a))})}
function cl(a,b){var c,d;return w(function(e){return 1==e.i?(c=[],t(e,Yk($k,b),2)):3!=e.i?(d=e.j,t(e,Ek(d,["databases"],{O:!0,mode:"readonly"},function(f){c.length=0;return Nk(f.objectStore("databases"),{},function(g){a(g.getValue())&&c.push(g.getValue());return g.continue()})}),3)):e.return(c)})}
function dl(a){return cl(function(b){return"LogsDatabaseV2"===b.publicName&&void 0!==b.userIdentifier},a)}
function el(a,b,c){return cl(function(d){return c?void 0!==d.userIdentifier&&!a.includes(d.userIdentifier)&&c.includes(d.publicName):void 0!==d.userIdentifier&&!a.includes(d.userIdentifier)},b)}
function fl(a){var b,c;return w(function(d){if(1==d.i)return b=ck("YtIdbMeta hasAnyMeta other"),t(d,cl(function(e){return void 0!==e.userIdentifier&&e.userIdentifier!==b},a),2);
c=d.j;return d.return(0<c.length)})}
;var gl,hl=new function(){}(new function(){});
function il(){var a,b,c,d;return w(function(e){switch(e.i){case 1:a=Rj();if(null==(b=a)?0:b.hasSucceededOnce)return e.return(!0);var f;if(f=Oj)f=/WebKit\/([0-9]+)/.exec(Qb()),f=!!(f&&600<=parseInt(f[1],10));f&&(f=/WebKit\/([0-9]+)/.exec(Qb()),f=!(f&&602<=parseInt(f[1],10)));if(f||tc)return e.return(!1);try{if(c=self,!(c.indexedDB&&c.IDBIndex&&c.IDBKeyRange&&c.IDBObjectStore))return e.return(!1)}catch(g){return e.return(!1)}if(!("IDBTransaction"in self&&"objectStoreNames"in IDBTransaction.prototype))return e.return(!1);
wa(e,2);d={actualName:"yt-idb-test-do-not-use",publicName:"yt-idb-test-do-not-use",userIdentifier:void 0};return t(e,al(d,hl),4);case 4:return t(e,bl("yt-idb-test-do-not-use",hl),5);case 5:return e.return(!0);case 2:return ya(e),e.return(!1)}})}
function jl(){if(void 0!==gl)return gl;Uj=!0;return gl=il().then(function(a){Uj=!1;var b;if(null!=(b=Qj())&&b.i){var c;b={hasSucceededOnce:(null==(c=Rj())?void 0:c.hasSucceededOnce)||a};var d;null==(d=Qj())||d.set("LAST_RESULT_ENTRY_KEY",b,2592E3,!0)}return a})}
function kl(){return A("ytglobal.idbToken_")||void 0}
function ll(){var a=kl();return a?Promise.resolve(a):jl().then(function(b){(b=b?hl:void 0)&&z("ytglobal.idbToken_",b);return b})}
;var ml=0;function nl(a,b){ml||(ml=af.M(function(){var c,d,e,f,g;return w(function(h){switch(h.i){case 1:return t(h,ll(),2);case 2:c=h.j;if(!c)return h.return();d=!0;wa(h,3);return t(h,el(a,c,b),5);case 5:e=h.j;if(!e.length){d=!1;h.u(6);break}f=e[0];return t(h,Uk(f.actualName),7);case 7:return t(h,bl(f.actualName,c),6);case 6:xa(h,4);break;case 3:g=ya(h),Zj(g),d=!1;case 4:af.S(ml),ml=0,d&&nl(a,b),h.i=0}})}))}
function ol(){var a;return w(function(b){return 1==b.i?t(b,ll(),2):(a=b.j)?b.return(fl(a)):b.return(!1)})}
new zd;function pl(a){if(!bk())throw a=new lk("AUTH_INVALID",{dbName:a}),Zj(a),a;var b=ck();return{actualName:a+":"+b,publicName:a,userIdentifier:b}}
function ql(a,b,c,d){var e,f,g,h,k,m;return w(function(p){switch(p.i){case 1:return f=null!=(e=Error().stack)?e:"",t(p,ll(),2);case 2:g=p.j;if(!g)throw h=qk("openDbImpl",a,b),M("ytidb_async_stack_killswitch")||(h.stack=h.stack+"\n"+f.substring(f.indexOf("\n")+1)),Zj(h),h;dk(a);k=c?{actualName:a,publicName:a,userIdentifier:void 0}:pl(a);wa(p,3);return t(p,al(k,g),5);case 5:return t(p,Tk(k.actualName,b,d),6);case 6:return p.return(p.j);case 3:return m=ya(p),wa(p,7),t(p,bl(k.actualName,g),9);case 9:xa(p,
8);break;case 7:ya(p);case 8:throw m;}})}
function rl(a,b,c){c=void 0===c?{}:c;return ql(a,b,!1,c)}
function sl(a,b,c){c=void 0===c?{}:c;return ql(a,b,!0,c)}
function tl(a,b){b=void 0===b?{}:b;var c,d;return w(function(e){if(1==e.i)return t(e,ll(),2);if(3!=e.i){c=e.j;if(!c)return e.return();dk(a);d=pl(a);return t(e,Uk(d.actualName,b),3)}return t(e,bl(d.actualName,c),0)})}
function ul(a,b,c){a=a.map(function(d){return w(function(e){return 1==e.i?t(e,Uk(d.actualName,b),2):t(e,bl(d.actualName,c),0)})});
return Promise.all(a).then(function(){})}
function vl(){var a=void 0===a?{}:a;var b,c;return w(function(d){if(1==d.i)return t(d,ll(),2);if(3!=d.i){b=d.j;if(!b)return d.return();dk("LogsDatabaseV2");return t(d,dl(b),3)}c=d.j;return t(d,ul(c,a,b),0)})}
function wl(a,b){b=void 0===b?{}:b;var c;return w(function(d){if(1==d.i)return t(d,ll(),2);if(3!=d.i){c=d.j;if(!c)return d.return();dk(a);return t(d,Uk(a,b),3)}return t(d,bl(a,c),0)})}
;function xl(a){this.Fa=this.i=!1;this.potentialEsfErrorCounter=this.j=0;this.handleError=function(){};
this.na=function(){};
this.now=Date.now;this.ta=!1;var b;this.Hb=null!=(b=a.Hb)?b:100;var c;this.Fb=null!=(c=a.Fb)?c:1;var d;this.Db=null!=(d=a.Db)?d:2592E6;var e;this.Bb=null!=(e=a.Bb)?e:12E4;var f;this.Eb=null!=(f=a.Eb)?f:5E3;var g;this.C=null!=(g=a.C)?g:void 0;this.Ja=!!a.Ja;var h;this.Ia=null!=(h=a.Ia)?h:.1;var k;this.Pa=null!=(k=a.Pa)?k:10;a.handleError&&(this.handleError=a.handleError);a.na&&(this.na=a.na);a.ta&&(this.ta=a.ta);a.Fa&&(this.Fa=a.Fa);this.G=a.G;this.T=a.T;this.K=a.K;this.J=a.J;this.ba=a.ba;this.fb=
a.fb;this.eb=a.eb;yl(this)&&(!this.G||this.G("networkless_logging"))&&zl(this)}
function zl(a){yl(a)&&!a.ta&&(a.i=!0,a.Ja&&Math.random()<=a.Ia&&a.K.Ob(a.C),Al(a),a.J.H()&&a.ya(),a.J.U(a.fb,a.ya.bind(a)),a.J.U(a.eb,a.kb.bind(a)))}
l=xl.prototype;l.writeThenSend=function(a,b){var c=this;b=void 0===b?{}:b;if(yl(this)&&this.i){var d={url:a,options:b,timestamp:this.now(),status:"NEW",sendCount:0};this.K.set(d,this.C).then(function(e){d.id=e;c.J.H()&&Bl(c,d)}).catch(function(e){Bl(c,d);
Cl(c,e)})}else this.ba(a,b)};
l.sendThenWrite=function(a,b,c){var d=this;b=void 0===b?{}:b;if(yl(this)&&this.i){var e={url:a,options:b,timestamp:this.now(),status:"NEW",sendCount:0};this.G&&this.G("nwl_skip_retry")&&(e.skipRetry=c);if(this.J.H()||this.G&&this.G("nwl_aggressive_send_then_write")&&!e.skipRetry){if(!e.skipRetry){var f=b.onError?b.onError:function(){};
b.onError=function(g,h){return w(function(k){if(1==k.i)return t(k,d.K.set(e,d.C).catch(function(m){Cl(d,m)}),2);
f(g,h);k.i=0})}}this.ba(a,b,e.skipRetry)}else this.K.set(e,this.C).catch(function(g){d.ba(a,b,e.skipRetry);
Cl(d,g)})}else this.ba(a,b,this.G&&this.G("nwl_skip_retry")&&c)};
l.sendAndWrite=function(a,b){var c=this;b=void 0===b?{}:b;if(yl(this)&&this.i){var d={url:a,options:b,timestamp:this.now(),status:"NEW",sendCount:0},e=!1,f=b.onSuccess?b.onSuccess:function(){};
d.options.onSuccess=function(g,h){void 0!==d.id?c.K.ma(d.id,c.C):e=!0;c.J.aa&&c.G&&c.G("vss_network_hint")&&c.J.aa(!0);f(g,h)};
this.ba(d.url,d.options);this.K.set(d,this.C).then(function(g){d.id=g;e&&c.K.ma(d.id,c.C)}).catch(function(g){Cl(c,g)})}else this.ba(a,b)};
l.ya=function(){var a=this;if(!yl(this))throw qk("throttleSend");this.j||(this.j=this.T.M(function(){var b;return w(function(c){if(1==c.i)return t(c,a.K.tb("NEW",a.C),2);if(3!=c.i)return b=c.j,b?t(c,Bl(a,b),3):(a.kb(),c.return());a.j&&(a.j=0,a.ya());c.i=0})},this.Hb))};
l.kb=function(){this.T.S(this.j);this.j=0};
function Bl(a,b){var c,d;return w(function(e){switch(e.i){case 1:if(!yl(a))throw c=qk("immediateSend"),c;if(void 0===b.id){e.u(2);break}return t(e,a.K.Yb(b.id,a.C),3);case 3:(d=e.j)?b=d:a.na(Error("The request cannot be found in the database."));case 2:if(Dl(a,b,a.Db)){e.u(4);break}a.na(Error("Networkless Logging: Stored logs request expired age limit"));if(void 0===b.id){e.u(5);break}return t(e,a.K.ma(b.id,a.C),5);case 5:return e.return();case 4:b.skipRetry||(b=El(a,b));if(!b){e.u(0);break}if(!b.skipRetry||
void 0===b.id){e.u(8);break}return t(e,a.K.ma(b.id,a.C),8);case 8:a.ba(b.url,b.options,!!b.skipRetry),e.i=0}})}
function El(a,b){if(!yl(a))throw qk("updateRequestHandlers");var c=b.options.onError?b.options.onError:function(){};
b.options.onError=function(e,f){var g,h,k;return w(function(m){switch(m.i){case 1:g=Fl(f);if(!(a.G&&a.G("nwl_consider_error_code")&&g||a.G&&!a.G("nwl_consider_error_code")&&a.potentialEsfErrorCounter<=a.Pa)){m.u(2);break}if(!a.J.ca){m.u(3);break}return t(m,a.J.ca(),3);case 3:if(a.J.H()){m.u(2);break}c(e,f);if(!a.G||!a.G("nwl_consider_error_code")||void 0===(null==(h=b)?void 0:h.id)){m.u(6);break}return t(m,a.K.gb(b.id,a.C,!1),6);case 6:return m.return();case 2:if(a.G&&a.G("nwl_consider_error_code")&&
!g&&a.potentialEsfErrorCounter>a.Pa)return m.return();a.potentialEsfErrorCounter++;if(void 0===(null==(k=b)?void 0:k.id)){m.u(8);break}return b.sendCount<a.Fb?t(m,a.K.gb(b.id,a.C),12):t(m,a.K.ma(b.id,a.C),8);case 12:a.T.M(function(){a.J.H()&&a.ya()},a.Eb);
case 8:c(e,f),m.i=0}})};
var d=b.options.onSuccess?b.options.onSuccess:function(){};
b.options.onSuccess=function(e,f){var g;return w(function(h){if(1==h.i)return void 0===(null==(g=b)?void 0:g.id)?h.u(2):t(h,a.K.ma(b.id,a.C),2);a.J.aa&&a.G&&a.G("vss_network_hint")&&a.J.aa(!0);d(e,f);h.i=0})};
return b}
function Dl(a,b,c){b=b.timestamp;return a.now()-b>=c?!1:!0}
function Al(a){if(!yl(a))throw qk("retryQueuedRequests");a.K.tb("QUEUED",a.C).then(function(b){b&&!Dl(a,b,a.Bb)?a.T.M(function(){return w(function(c){if(1==c.i)return void 0===b.id?c.u(2):t(c,a.K.gb(b.id,a.C),2);Al(a);c.i=0})}):a.J.H()&&a.ya()})}
function Cl(a,b){a.Jb&&!a.J.H()?a.Jb(b):a.handleError(b)}
function yl(a){return!!a.C||a.Fa}
function Fl(a){var b;return(a=null==a?void 0:null==(b=a.error)?void 0:b.code)&&400<=a&&599>=a?!1:!0}
;function Gl(a,b){this.version=a;this.args=b}
;function Hl(a,b){this.topic=a;this.i=b}
Hl.prototype.toString=function(){return this.topic};var Il=A("ytPubsub2Pubsub2Instance")||new K;K.prototype.subscribe=K.prototype.subscribe;K.prototype.unsubscribeByKey=K.prototype.ra;K.prototype.publish=K.prototype.ha;K.prototype.clear=K.prototype.clear;z("ytPubsub2Pubsub2Instance",Il);var Jl=A("ytPubsub2Pubsub2SubscribedKeys")||{};z("ytPubsub2Pubsub2SubscribedKeys",Jl);var Kl=A("ytPubsub2Pubsub2TopicToKeys")||{};z("ytPubsub2Pubsub2TopicToKeys",Kl);var Ll=A("ytPubsub2Pubsub2IsAsync")||{};z("ytPubsub2Pubsub2IsAsync",Ll);
z("ytPubsub2Pubsub2SkipSubKey",null);function Ml(a,b){var c=Nl();c&&c.publish.call(c,a.toString(),a,b)}
function Ol(a){var b=Pl,c=Nl();if(!c)return 0;var d=c.subscribe(b.toString(),function(e,f){var g=A("ytPubsub2Pubsub2SkipSubKey");g&&g==d||(g=function(){if(Jl[d])try{if(f&&b instanceof Hl&&b!=e)try{var h=b.i,k=f;if(!k.args||!k.version)throw Error("yt.pubsub2.Data.deserialize(): serializedData is incomplete.");try{if(!h.ga){var m=new h;h.ga=m.version}var p=h.ga}catch(H){}if(!p||k.version!=p)throw Error("yt.pubsub2.Data.deserialize(): serializedData version is incompatible.");try{p=Reflect;var u=p.construct;
var x=k.args,v=x.length;if(0<v){var E=Array(v);for(k=0;k<v;k++)E[k]=x[k];var G=E}else G=[];f=u.call(p,h,G)}catch(H){throw H.message="yt.pubsub2.Data.deserialize(): "+H.message,H;}}catch(H){throw H.message="yt.pubsub2.pubsub2 cross-binary conversion error for "+b.toString()+": "+H.message,H;}a.call(window,f)}catch(H){Vh(H)}},Ll[b.toString()]?A("yt.scheduler.instance")?af.M(g):oi(g,0):g())});
Jl[d]=!0;Kl[b.toString()]||(Kl[b.toString()]=[]);Kl[b.toString()].push(d);return d}
function Ql(){var a=Rl,b=Ol(function(c){a.apply(void 0,arguments);Sl(b)});
return b}
function Sl(a){var b=Nl();b&&("number"===typeof a&&(a=[a]),eb(a,function(c){b.unsubscribeByKey(c);delete Jl[c]}))}
function Nl(){return A("ytPubsub2Pubsub2Instance")}
;function Tl(a,b){Wk.call(this,a,b);this.options=b;dk(a)}
r(Tl,Wk);function Ul(a,b){var c;return function(){c||(c=new Tl(a,b));return c}}
Tl.prototype.l=function(a,b,c){c=void 0===c?{}:c;return(this.options.hb?sl:rl)(a,b,Object.assign({},c))};
Tl.prototype.delete=function(a){a=void 0===a?{}:a;return(this.options.hb?wl:tl)(this.name,a)};
function Vl(a,b){return Ul(a,b)}
;var Wl;
function Xl(){if(Wl)return Wl();var a={};Wl=Vl("LogsDatabaseV2",{va:(a.LogsRequestsStore={Ua:2},a),hb:!1,upgrade:function(b,c,d){c(2)&&Fk(b,"LogsRequestsStore",{keyPath:"id",autoIncrement:!0});c(3);c(5)&&(d=d.objectStore("LogsRequestsStore"),d.i.indexNames.contains("newRequest")&&d.i.deleteIndex("newRequest"),d.i.createIndex("newRequestV2",["status","interface","timestamp"],{unique:!1}));c(7)&&b.i.objectStoreNames.contains("sapisid")&&b.i.deleteObjectStore("sapisid");c(9)&&b.i.objectStoreNames.contains("SWHealthLog")&&b.i.deleteObjectStore("SWHealthLog")},
version:9});return Wl()}
;function Yl(a){return Yk(Xl(),a)}
function Zl(a,b){var c,d,e,f;return w(function(g){if(1==g.i)return c={startTime:P(),transactionType:"YT_IDB_TRANSACTION_TYPE_WRITE"},t(g,Yl(b),2);if(3!=g.i)return d=g.j,e=Object.assign({},a,{options:JSON.parse(JSON.stringify(a.options)),interface:L("INNERTUBE_CONTEXT_CLIENT_NAME",0)}),t(g,Hk(d,e),3);f=g.j;c.zc=P();$l(c);return g.return(f)})}
function am(a,b){var c,d,e,f,g,h,k;return w(function(m){if(1==m.i)return c={startTime:P(),transactionType:"YT_IDB_TRANSACTION_TYPE_READ"},t(m,Yl(b),2);if(3!=m.i)return d=m.j,e=L("INNERTUBE_CONTEXT_CLIENT_NAME",0),f=[a,e,0],g=[a,e,P()],h=IDBKeyRange.bound(f,g),k=void 0,t(m,Ek(d,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(p){return Qk(p.objectStore("LogsRequestsStore").index("newRequestV2"),{query:h,direction:"prev"},function(u){u.getValue()&&(k=u.getValue(),"NEW"===a&&(k.status="QUEUED",
u.update(k)))})}),3);
c.zc=P();$l(c);return m.return(k)})}
function bm(a,b){var c;return w(function(d){if(1==d.i)return t(d,Yl(b),2);c=d.j;return d.return(Ek(c,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(e){var f=e.objectStore("LogsRequestsStore");return f.get(a).then(function(g){if(g)return g.status="QUEUED",Bk(f.i.put(g,void 0)).then(function(){return g})})}))})}
function cm(a,b,c){c=void 0===c?!0:c;var d;return w(function(e){if(1==e.i)return t(e,Yl(b),2);d=e.j;return e.return(Ek(d,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(f){var g=f.objectStore("LogsRequestsStore");return g.get(a).then(function(h){return h?(h.status="NEW",c&&(h.sendCount+=1),Bk(g.i.put(h,void 0)).then(function(){return h})):uk.resolve(void 0)})}))})}
function dm(a,b){var c;return w(function(d){if(1==d.i)return t(d,Yl(b),2);c=d.j;return d.return(c.delete("LogsRequestsStore",a))})}
function em(a){var b,c;return w(function(d){if(1==d.i)return t(d,Yl(a),2);b=d.j;c=P()-2592E6;return t(d,Ek(b,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(e){return Nk(e.objectStore("LogsRequestsStore"),{},function(f){if(f.getValue().timestamp<=c)return f.delete().then(function(){return f.continue()})})}),0)})}
function fm(){return w(function(a){return t(a,vl(),0)})}
function $l(a){M("nwl_csi_killswitch")||.01>=Math.random()&&Ml("nwl_transaction_latency_payload",a)}
;var gm={},hm=Vl("ServiceWorkerLogsDatabase",{va:(gm.SWHealthLog={Ua:1},gm),hb:!0,upgrade:function(a,b){b(1)&&Fk(a,"SWHealthLog",{keyPath:"id",autoIncrement:!0}).i.createIndex("swHealthNewRequest",["interface","timestamp"],{unique:!1})},
version:1});function im(a){return Yk(hm(),a)}
function jm(a){var b,c;return w(function(d){if(1==d.i)return t(d,im(a),2);b=d.j;c=P()-2592E6;return t(d,Ek(b,["SWHealthLog"],{mode:"readwrite",O:!0},function(e){return Nk(e.objectStore("SWHealthLog"),{},function(f){if(f.getValue().timestamp<=c)return f.delete().then(function(){return f.continue()})})}),0)})}
function km(a){var b;return w(function(c){if(1==c.i)return t(c,im(a),2);b=c.j;return t(c,b.clear("SWHealthLog"),0)})}
;var lm={},mm=0;
function nm(a){var b=void 0===b?"":b;var c=void 0===c?!1:c;if(a)if(b)Ti(a,void 0,"POST",b);else if(L("USE_NET_AJAX_FOR_PING_TRANSPORT",!1))Ti(a,void 0,"GET","",void 0,void 0,c);else{b:{try{var d=new ab({url:a});if(d.l&&d.j||d.m){var e=ac(bc(5,a)),f;if(!(f=!e||!e.endsWith("/aclk"))){var g=a.search(ic),h=hc(a,0,"ri",g);if(0>h)var k=null;else{var m=a.indexOf("&",h);if(0>m||m>g)m=g;k=decodeURIComponent(a.slice(h+3,-1!==m?m:0).replace(/\+/g," "))}f="1"!==k}var p=!f;break b}}catch(x){}p=!1}if(p){b:{try{if(window.navigator&&
window.navigator.sendBeacon&&window.navigator.sendBeacon(a,"")){var u=!0;break b}}catch(x){}u=!1}b=u?!0:!1}else b=!1;b||om(a)}}
function om(a){var b=new Image,c=""+mm++;lm[c]=b;b.onload=b.onerror=function(){delete lm[c]};
b.src=a}
;function pm(){this.i=new Map;this.j=!1}
function qm(){if(!pm.i){var a=A("yt.networkRequestMonitor.instance")||new pm;z("yt.networkRequestMonitor.instance",a);pm.i=a}return pm.i}
pm.prototype.requestComplete=function(a,b){b&&(this.j=!0);a=this.removeParams(a);this.i.get(a)||this.i.set(a,b)};
pm.prototype.isEndpointCFR=function(a){a=this.removeParams(a);return(a=this.i.get(a))?!1:!1===a&&this.j?!0:null};
pm.prototype.removeParams=function(a){return a.split("?")[0]};
pm.prototype.removeParams=pm.prototype.removeParams;pm.prototype.isEndpointCFR=pm.prototype.isEndpointCFR;pm.prototype.requestComplete=pm.prototype.requestComplete;pm.getInstance=qm;var rm;function sm(){rm||(rm=new zj("yt.offline"));return rm}
function tm(a){if(M("offline_error_handling")){var b=sm().get("errors",!0)||{};b[a.message]={name:a.name,stack:a.stack};a.level&&(b[a.message].level=a.level);sm().set("errors",b,2592E3,!0)}}
function um(){if(M("offline_error_handling")){var a=sm().get("errors",!0);if(a){for(var b in a)if(a[b]){var c=new Q(b,"sent via offline_errors");c.name=a[b].name;c.stack=a[b].stack;c.level=a[b].level;Vh(c)}sm().set("errors",{},2592E3,!0)}}}
;var vm=Ph("network_polling_interval",3E4);function wm(){Ue.call(this);var a=this;this.R=0;this.A=this.o=!1;this.l=this.Ya();M("use_shared_nsm")?(this.j=$e(),M("use_shared_nsm_and_keep_yt_online_updated")&&(this.j.U("networkstatus-online",function(){a.l=!0;a.A&&um()}),this.j.U("networkstatus-offline",function(){a.l=!1}))):(xm(this),ym(this))}
r(wm,Ue);function zm(){if(!wm.i){var a=A("yt.networkStatusManager.instance")||new wm;z("yt.networkStatusManager.instance",a);wm.i=a}return wm.i}
l=wm.prototype;l.H=function(){if(M("use_shared_nsm")&&this.j){var a;return null==(a=this.j)?void 0:a.H()}return this.l};
l.aa=function(a){if(M("use_shared_nsm")&&this.j){var b;null!=(b=this.j)&&(b.j=a)}else a!==this.l&&(this.l=a)};
l.Zb=function(a){!M("use_shared_nsm")&&(this.o=!0,void 0===a?0:a)&&(this.R||Am(this))};
l.Ya=function(){var a=window.navigator.onLine;return void 0===a?!0:a};
l.Rb=function(){this.A=!0};
l.U=function(a,b){return M("use_shared_nsm")&&this.j?this.j.U(a,b):Ue.prototype.U.call(this,a,b)};
function ym(a){window.addEventListener("online",function(){return w(function(b){if(1==b.i)return t(b,a.ca(),2);a.A&&um();b.i=0})})}
function xm(a){window.addEventListener("offline",function(){return w(function(b){return t(b,a.ca(),0)})})}
function Am(a){a.R=Ij(function(){return w(function(b){if(1==b.i)return a.l?a.Ya()||!a.o?b.u(3):t(b,a.ca(),3):t(b,a.ca(),3);Am(a);b.i=0})},vm)}
l.ca=function(a){var b=this;if(M("use_shared_nsm")&&this.j){var c=Ye(this.j,a);c.then(function(d){M("use_cfr_monitor")&&qm().requestComplete("generate_204",d)});
return c}return this.v?this.v:this.v=new Promise(function(d){var e,f,g,h;return w(function(k){switch(k.i){case 1:return e=window.AbortController?new window.AbortController:void 0,g=null==(f=e)?void 0:f.signal,h=!1,wa(k,2,3),e&&(b.N=af.M(function(){e.abort()},a||2E4)),t(k,fetch("/generate_204",{method:"HEAD",
signal:g}),5);case 5:h=!0;case 3:za(k);M("use_cfr_monitor")&&qm().requestComplete("generate_204",h);b.v=void 0;b.N&&af.S(b.N);h!==b.l&&(b.l=h,b.l&&b.o?Ve(b,"ytnetworkstatus-online"):b.o&&Ve(b,"ytnetworkstatus-offline"));d(h);Aa(k);break;case 2:ya(k),h=!1,k.u(3)}})})};
wm.prototype.sendNetworkCheckRequest=wm.prototype.ca;wm.prototype.listen=wm.prototype.U;wm.prototype.enableErrorFlushing=wm.prototype.Rb;wm.prototype.getWindowStatus=wm.prototype.Ya;wm.prototype.monitorNetworkStatusChange=wm.prototype.Zb;wm.prototype.networkStatusHint=wm.prototype.aa;wm.prototype.isNetworkAvailable=wm.prototype.H;wm.getInstance=zm;function Bm(a){a=void 0===a?{}:a;Ue.call(this);var b=this;this.l=this.N=0;this.o="ytnetworkstatus-offline";this.v="ytnetworkstatus-online";M("use_shared_nsm")&&(this.o="networkstatus-offline",this.v="networkstatus-online");this.j=zm();var c=A("yt.networkStatusManager.instance.monitorNetworkStatusChange").bind(this.j);c&&c(a.qb);a.yb&&(c=A("yt.networkStatusManager.instance.enableErrorFlushing").bind(this.j))&&c();if(c=A("yt.networkStatusManager.instance.listen").bind(this.j))a.Ra?(this.Ra=a.Ra,c(this.v,
function(){Cm(b,"publicytnetworkstatus-online")}),c(this.o,function(){Cm(b,"publicytnetworkstatus-offline")})):(c(this.v,function(){Ve(b,"publicytnetworkstatus-online")}),c(this.o,function(){Ve(b,"publicytnetworkstatus-offline")}))}
r(Bm,Ue);Bm.prototype.H=function(){var a=A("yt.networkStatusManager.instance.isNetworkAvailable");return a?a.bind(this.j)():!0};
Bm.prototype.aa=function(a){var b=A("yt.networkStatusManager.instance.networkStatusHint").bind(this.j);b&&b(a)};
Bm.prototype.ca=function(a){var b=this,c;return w(function(d){c=A("yt.networkStatusManager.instance.sendNetworkCheckRequest").bind(b.j);return M("skip_network_check_if_cfr")&&qm().isEndpointCFR("generate_204")?d.return(new Promise(function(e){var f;b.aa((null==(f=window.navigator)?void 0:f.onLine)||!0);e(b.H())})):c?d.return(c(a)):d.return(!0)})};
function Cm(a,b){a.Ra?a.l?(af.S(a.N),a.N=af.M(function(){a.A!==b&&(Ve(a,b),a.A=b,a.l=P())},a.Ra-(P()-a.l))):(Ve(a,b),a.A=b,a.l=P()):Ve(a,b)}
;var Dm;function Em(){xl.call(this,{K:{Ob:em,ma:dm,tb:am,Yb:bm,gb:cm,set:Zl},J:Fm(),handleError:Vh,na:Wh,ba:Gm,now:P,Jb:tm,T:Nj(),fb:"publicytnetworkstatus-online",eb:"publicytnetworkstatus-offline",Ja:!0,Ia:.1,Pa:Ph("potential_esf_error_limit",10),G:M,ta:!(bk()&&Hm())});this.l=new zd;M("networkless_immediately_drop_all_requests")&&fm();wl("LogsDatabaseV2")}
r(Em,xl);function Im(){var a=A("yt.networklessRequestController.instance");a||(a=new Em,z("yt.networklessRequestController.instance",a),M("networkless_logging")&&ll().then(function(b){a.C=b;zl(a);a.l.resolve();a.Ja&&Math.random()<=a.Ia&&a.C&&jm(a.C);M("networkless_immediately_drop_sw_health_store")&&Jm(a)}));
return a}
Em.prototype.writeThenSend=function(a,b){b||(b={});bk()||(this.i=!1);xl.prototype.writeThenSend.call(this,a,b)};
Em.prototype.sendThenWrite=function(a,b,c){b||(b={});bk()||(this.i=!1);xl.prototype.sendThenWrite.call(this,a,b,c)};
Em.prototype.sendAndWrite=function(a,b){b||(b={});bk()||(this.i=!1);xl.prototype.sendAndWrite.call(this,a,b)};
Em.prototype.awaitInitialization=function(){return this.l.promise};
function Jm(a){var b;w(function(c){if(!a.C)throw b=qk("clearSWHealthLogsDb"),b;return c.return(km(a.C).catch(function(d){a.handleError(d)}))})}
function Gm(a,b,c){M("use_cfr_monitor")&&Km(a,b);var d;if(null==(d=b.postParams)?0:d.requestTimeMs)b.postParams.requestTimeMs=Math.round(P());c&&0===Object.keys(b).length?nm(a):vi(a,b)}
function Fm(){Dm||(Dm=new Bm({yb:!0,qb:!0}));return Dm}
function Km(a,b){var c=b.onError?b.onError:function(){};
b.onError=function(e,f){qm().requestComplete(a,!1);c(e,f)};
var d=b.onSuccess?b.onSuccess:function(){};
b.onSuccess=function(e,f){qm().requestComplete(a,!0);d(e,f)}}
function Hm(){return M("embeds_web_nwl_disable_nocookie")?"www.youtube-nocookie.com"!==cc(document.location.toString()):!0}
;var Lm=!1,Mm=0,Nm=0,Om,Pm=y.ytNetworklessLoggingInitializationOptions||{isNwlInitialized:Lm,potentialEsfErrorCounter:Nm};z("ytNetworklessLoggingInitializationOptions",Pm);
function Qm(){var a;w(function(b){switch(b.i){case 1:return t(b,ll(),2);case 2:a=b.j;if(!a||!bk()&&!M("nwl_init_require_datasync_id_killswitch")||!Hm()){b.u(0);break}Lm=!0;Pm.isNwlInitialized=Lm;if(!M("use_new_nwl_initialization")){b.u(4);break}return t(b,Im().awaitInitialization(),5);case 5:return b.return();case 4:return t(b,wl("LogsDatabaseV2"),6);case 6:if(!(.1>=Math.random())){b.u(7);break}return t(b,em(a),8);case 8:return t(b,jm(a),7);case 7:Rm();Sm().H()&&Tm();Sm().U("publicytnetworkstatus-online",
Tm);Sm().U("publicytnetworkstatus-offline",Um);if(!M("networkless_immediately_drop_sw_health_store")){b.u(10);break}return t(b,Vm(),10);case 10:if(M("networkless_immediately_drop_all_requests"))return t(b,fm(),0);b.u(0)}})}
function Wm(a,b){function c(d){var e=Sm().H();if(!Xm()||!d||e&&M("vss_networkless_bypass_write"))Ym(a,b);else{var f={url:a,options:b,timestamp:P(),status:"NEW",sendCount:0};Zl(f,d).then(function(g){f.id=g;Sm().H()&&Zm(f)}).catch(function(g){Zm(f);
Sm().H()?Vh(g):tm(g)})}}
b=void 0===b?{}:b;M("skip_is_supported_killswitch")?ll().then(function(d){c(d)}):c(kl())}
function $m(a,b){function c(d){if(Xm()&&d){var e={url:a,options:b,timestamp:P(),status:"NEW",sendCount:0},f=!1,g=b.onSuccess?b.onSuccess:function(){};
e.options.onSuccess=function(k,m){M("use_cfr_monitor")&&qm().requestComplete(e.url,!0);void 0!==e.id?dm(e.id,d):f=!0;M("vss_network_hint")&&Sm().aa(!0);g(k,m)};
if(M("use_cfr_monitor")){var h=b.onError?b.onError:function(){};
e.options.onError=function(k,m){qm().requestComplete(e.url,!1);h(k,m)}}Ym(e.url,e.options);
Zl(e,d).then(function(k){e.id=k;f&&dm(e.id,d)}).catch(function(k){Sm().H()?Vh(k):tm(k)})}else Ym(a,b)}
b=void 0===b?{}:b;M("skip_is_supported_killswitch")?ll().then(function(d){c(d)}):c(kl())}
function Tm(){var a=kl();if(!a)throw qk("throttleSend");Mm||(Mm=af.M(function(){var b;return w(function(c){if(1==c.i)return t(c,am("NEW",a),2);if(3!=c.i)return b=c.j,b?t(c,Zm(b),3):(Um(),c.return());Mm&&(Mm=0,Tm());c.i=0})},100))}
function Um(){af.S(Mm);Mm=0}
function Zm(a){var b,c,d;return w(function(e){switch(e.i){case 1:b=kl();if(!b)throw c=qk("immediateSend"),c;if(void 0===a.id){e.u(2);break}return t(e,bm(a.id,b),3);case 3:(d=e.j)?a=d:Wh(Error("The request cannot be found in the database."));case 2:if(an(a,2592E6)){e.u(4);break}Wh(Error("Networkless Logging: Stored logs request expired age limit"));if(void 0===a.id){e.u(5);break}return t(e,dm(a.id,b),5);case 5:return e.return();case 4:a.skipRetry||(a=bn(a));var f=a,g,h;if(null==f?0:null==(g=f.options)?
0:null==(h=g.postParams)?0:h.requestTimeMs)f.options.postParams.requestTimeMs=Math.round(P());a=f;if(!a){e.u(0);break}if(!a.skipRetry||void 0===a.id){e.u(8);break}return t(e,dm(a.id,b),8);case 8:Ym(a.url,a.options,!!a.skipRetry),e.i=0}})}
function bn(a){var b=kl();if(!b)throw qk("updateRequestHandlers");var c=a.options.onError?a.options.onError:function(){};
a.options.onError=function(e,f){var g,h,k;return w(function(m){switch(m.i){case 1:M("use_cfr_monitor")&&qm().requestComplete(a.url,!1);g=Fl(f);if(!(M("nwl_consider_error_code")&&g||!M("nwl_consider_error_code")&&cn()<=Ph("potential_esf_error_limit",10))){m.u(2);break}if(M("skip_checking_network_on_cfr_failure")&&(!M("skip_checking_network_on_cfr_failure")||qm().isEndpointCFR(a.url))){m.u(3);break}return t(m,Sm().ca(),3);case 3:if(Sm().H()){m.u(2);break}c(e,f);if(!M("nwl_consider_error_code")||void 0===
(null==(h=a)?void 0:h.id)){m.u(6);break}return t(m,cm(a.id,b,!1),6);case 6:return m.return();case 2:if(M("nwl_consider_error_code")&&!g&&cn()>Ph("potential_esf_error_limit",10))return m.return();A("ytNetworklessLoggingInitializationOptions")&&Pm.potentialEsfErrorCounter++;Nm++;if(void 0===(null==(k=a)?void 0:k.id)){m.u(8);break}return 1>a.sendCount?t(m,cm(a.id,b),12):t(m,dm(a.id,b),8);case 12:af.M(function(){Sm().H()&&Tm()},5E3);
case 8:c(e,f),m.i=0}})};
var d=a.options.onSuccess?a.options.onSuccess:function(){};
a.options.onSuccess=function(e,f){var g;return w(function(h){if(1==h.i)return M("use_cfr_monitor")&&qm().requestComplete(a.url,!0),void 0===(null==(g=a)?void 0:g.id)?h.u(2):t(h,dm(a.id,b),2);M("vss_network_hint")&&Sm().aa(!0);d(e,f);h.i=0})};
return a}
function an(a,b){a=a.timestamp;return P()-a>=b?!1:!0}
function Rm(){var a=kl();if(!a)throw qk("retryQueuedRequests");am("QUEUED",a).then(function(b){b&&!an(b,12E4)?af.M(function(){return w(function(c){if(1==c.i)return void 0===b.id?c.u(2):t(c,cm(b.id,a),2);Rm();c.i=0})}):Sm().H()&&Tm()})}
function Vm(){var a,b;return w(function(c){a=kl();if(!a)throw b=qk("clearSWHealthLogsDb"),b;return c.return(km(a).catch(function(d){Vh(d)}))})}
function Sm(){if(M("use_new_nwl"))return Fm();Om||(Om=new Bm({yb:!0,qb:!0}));return Om}
function Ym(a,b,c){c&&0===Object.keys(b).length?nm(a):vi(a,b)}
function Xm(){return A("ytNetworklessLoggingInitializationOptions")?Pm.isNwlInitialized:Lm}
function cn(){return A("ytNetworklessLoggingInitializationOptions")?Pm.potentialEsfErrorCounter:Nm}
;function dn(a){var b=this;this.config_=null;a?this.config_=a:pj()&&(this.config_=qj());Ij(function(){Fj(b)},5E3)}
dn.prototype.isReady=function(){!this.config_&&pj()&&(this.config_=qj());return!!this.config_};
function Gj(a,b,c,d){function e(E){E=void 0===E?!1:E;var G;if(d.retry&&"www.youtube-nocookie.com"!=h&&(E||M("skip_ls_gel_retry")||"application/json"!==g.headers["Content-Type"]||(G=Dj(b,c,m,k)),G)){var H=g.onSuccess,R=g.onFetchSuccess;g.onSuccess=function(N,S){Ej(G);H(N,S)};
c.onFetchSuccess=function(N,S){Ej(G);R(N,S)}}try{E&&d.retry&&!d.zb.bypassNetworkless?(g.method="POST",d.zb.writeThenSend?M("use_new_nwl_wts")?Im().writeThenSend(v,g):Wm(v,g):M("use_new_nwl_saw")?Im().sendAndWrite(v,g):$m(v,g)):(g.method="POST",g.postParams||(g.postParams={}),vi(v,g))}catch(N){if("InvalidAccessError"==N.name)G&&(Ej(G),G=0),Wh(Error("An extension is blocking network request."));
else throw N;}G&&Ij(function(){Fj(a)},5E3)}
!L("VISITOR_DATA")&&"visitor_id"!==b&&.01>Math.random()&&Wh(new Q("Missing VISITOR_DATA when sending innertube request.",b,c,d));if(!a.isReady()){var f=new Q("innertube xhrclient not ready",b,c,d);Vh(f);throw f;}var g={headers:d.headers||{},method:"POST",postParams:c,postBody:d.postBody,postBodyFormat:d.postBodyFormat||"JSON",onTimeout:function(){d.onTimeout()},
onFetchTimeout:d.onTimeout,onSuccess:function(E,G){if(d.onSuccess)d.onSuccess(G)},
onFetchSuccess:function(E){if(d.onSuccess)d.onSuccess(E)},
onError:function(E,G){if(d.onError)d.onError(G)},
onFetchError:function(E){if(d.onError)d.onError(E)},
timeout:d.timeout,withCredentials:!0};g.headers["Content-Type"]||(g.headers["Content-Type"]="application/json");var h="";(f=a.config_.Vb)&&(h=f);var k=a.config_.Xb||!1,m=xj(k,h,d);Object.assign(g.headers,m);(f=g.headers.Authorization)&&!h&&(g.headers["x-origin"]=window.location.origin);var p="/youtubei/"+a.config_.innertubeApiVersion+"/"+b,u={alt:"json"},x=a.config_.Wb&&f;x=x&&f.startsWith("Bearer");x||(u.key=a.config_.innertubeApiKey);var v=hi(""+h+p,u||{},!0);M("use_new_nwl")&&Im().i||!M("use_new_nwl")&&
Xm()?jl().then(function(E){e(E)}):e(!1)}
;var en={appSettingsCaptured:!0,visualElementAttached:!0,visualElementGestured:!0,visualElementHidden:!0,visualElementShown:!0,flowEvent:!0,visualElementStateChanged:!0,playbackAssociated:!0,youThere:!0,accountStateChangeSignedIn:!0,accountStateChangeSignedOut:!0},fn={latencyActionBaselined:!0,latencyActionInfo:!0,latencyActionTicked:!0,bedrockRepetitiveActionTimed:!0,adsClientStateChange:!0,streamzIncremented:!0,mdxDialAdditionalDataUpdateEvent:!0,tvhtml5WatchKeyEvent:!0,tvhtml5VideoSeek:!0,tokenRefreshEvent:!0,
adNotify:!0,adNotifyFilled:!0,tvhtml5LaunchUrlComponentChanged:!0,bedrockResourceConsumptionSnapshot:!0,deviceStartupMetrics:!0,mdxSignIn:!0,tvhtml5KeyboardLogging:!0,tvhtml5StartupSoundEvent:!0,tvhtml5LiveChatStatus:!0,tvhtml5DeviceStorageStatus:!0,tvhtml5LocalStorage:!0,directSignInEvent:!0,finalPayload:!0,tvhtml5SearchCompleted:!0,tvhtml5KeyboardPerformance:!0,adNotifyFailure:!0,latencyActionSpan:!0,tvhtml5AccountDialogOpened:!0,tvhtml5ApiTest:!0};var gn=0,hn=vc?"webkit":uc?"moz":sc?"ms":rc?"o":"";z("ytDomDomGetNextId",A("ytDomDomGetNextId")||function(){return++gn});var jn={stopImmediatePropagation:1,stopPropagation:1,preventMouseEvent:1,preventManipulation:1,preventDefault:1,layerX:1,layerY:1,screenX:1,screenY:1,scale:1,rotation:1,webkitMovementX:1,webkitMovementY:1};
function kn(a){this.type="";this.state=this.source=this.data=this.currentTarget=this.relatedTarget=this.target=null;this.charCode=this.keyCode=0;this.metaKey=this.shiftKey=this.ctrlKey=this.altKey=!1;this.rotation=this.clientY=this.clientX=0;this.scale=1;this.changedTouches=this.touches=null;try{if(a=a||window.event){this.event=a;for(var b in a)b in jn||(this[b]=a[b]);this.scale=a.scale;this.rotation=a.rotation;var c=a.target||a.srcElement;c&&3==c.nodeType&&(c=c.parentNode);this.target=c;var d=a.relatedTarget;
if(d)try{d=d.nodeName?d:null}catch(e){d=null}else"mouseover"==this.type?d=a.fromElement:"mouseout"==this.type&&(d=a.toElement);this.relatedTarget=d;this.clientX=void 0!=a.clientX?a.clientX:a.pageX;this.clientY=void 0!=a.clientY?a.clientY:a.pageY;this.keyCode=a.keyCode?a.keyCode:a.which;this.charCode=a.charCode||("keypress"==this.type?this.keyCode:0);this.altKey=a.altKey;this.ctrlKey=a.ctrlKey;this.shiftKey=a.shiftKey;this.metaKey=a.metaKey;this.i=a.pageX;this.j=a.pageY}}catch(e){}}
function ln(a){if(document.body&&document.documentElement){var b=document.body.scrollTop+document.documentElement.scrollTop;a.i=a.clientX+(document.body.scrollLeft+document.documentElement.scrollLeft);a.j=a.clientY+b}}
kn.prototype.preventDefault=function(){this.event&&(this.event.returnValue=!1,this.event.preventDefault&&this.event.preventDefault())};
kn.prototype.stopPropagation=function(){this.event&&(this.event.cancelBubble=!0,this.event.stopPropagation&&this.event.stopPropagation())};
kn.prototype.stopImmediatePropagation=function(){this.event&&(this.event.cancelBubble=!0,this.event.stopImmediatePropagation&&this.event.stopImmediatePropagation())};var nb=y.ytEventsEventsListeners||{};z("ytEventsEventsListeners",nb);var mn=y.ytEventsEventsCounter||{count:0};z("ytEventsEventsCounter",mn);
function nn(a,b,c,d){d=void 0===d?{}:d;a.addEventListener&&("mouseenter"!=b||"onmouseenter"in document?"mouseleave"!=b||"onmouseenter"in document?"mousewheel"==b&&"MozBoxSizing"in document.documentElement.style&&(b="MozMousePixelScroll"):b="mouseout":b="mouseover");return mb(function(e){var f="boolean"===typeof e[4]&&e[4]==!!d,g=Qa(e[4])&&Qa(d)&&tb(e[4],d);return!!e.length&&e[0]==a&&e[1]==b&&e[2]==c&&(f||g)})}
var on=cb(function(){var a=!1;try{var b=Object.defineProperty({},"capture",{get:function(){a=!0}});
window.addEventListener("test",null,b)}catch(c){}return a});
function pn(a,b,c,d){d=void 0===d?{}:d;if(!a||!a.addEventListener&&!a.attachEvent)return"";var e=nn(a,b,c,d);if(e)return e;e=++mn.count+"";var f=!("mouseenter"!=b&&"mouseleave"!=b||!a.addEventListener||"onmouseenter"in document);var g=f?function(h){h=new kn(h);if(!Md(h.relatedTarget,function(k){return k==a}))return h.currentTarget=a,h.type=b,c.call(a,h)}:function(h){h=new kn(h);
h.currentTarget=a;return c.call(a,h)};
g=Uh(g);a.addEventListener?("mouseenter"==b&&f?b="mouseover":"mouseleave"==b&&f?b="mouseout":"mousewheel"==b&&"MozBoxSizing"in document.documentElement.style&&(b="MozMousePixelScroll"),on()||"boolean"===typeof d?a.addEventListener(b,g,d):a.addEventListener(b,g,!!d.capture)):a.attachEvent("on"+b,g);nb[e]=[a,b,c,g,d];return e}
function qn(a){a&&("string"==typeof a&&(a=[a]),eb(a,function(b){if(b in nb){var c=nb[b],d=c[0],e=c[1],f=c[3];c=c[4];d.removeEventListener?on()||"boolean"===typeof c?d.removeEventListener(e,f,c):d.removeEventListener(e,f,!!c.capture):d.detachEvent&&d.detachEvent("on"+e,f);delete nb[b]}}))}
;var rn=window.ytcsi&&window.ytcsi.now?window.ytcsi.now:window.performance&&window.performance.timing&&window.performance.now&&window.performance.timing.navigationStart?function(){return window.performance.timing.navigationStart+window.performance.now()}:function(){return(new Date).getTime()};function sn(a){this.L=a;this.j=null;this.o=0;this.A=null;this.v=0;this.l=[];for(a=0;4>a;a++)this.l.push(0);this.m=0;this.R=pn(window,"mousemove",Wa(this.W,this));a=Wa(this.N,this);"function"===typeof a&&(a=Uh(a));this.da=window.setInterval(a,25)}
Ya(sn,J);sn.prototype.W=function(a){void 0===a.i&&ln(a);var b=a.i;void 0===a.j&&ln(a);this.j=new Id(b,a.j)};
sn.prototype.N=function(){if(this.j){var a=rn();if(0!=this.o){var b=this.A,c=this.j,d=b.x-c.x;b=b.y-c.y;d=Math.sqrt(d*d+b*b)/(a-this.o);this.l[this.m]=.5<Math.abs((d-this.v)/this.v)?1:0;for(c=b=0;4>c;c++)b+=this.l[c]||0;3<=b&&this.L();this.v=d}this.o=a;this.A=this.j;this.m=(this.m+1)%4}};
sn.prototype.I=function(){window.clearInterval(this.da);qn(this.R)};var tn={};
function un(a){var b=void 0===a?{}:a;a=void 0===b.dc?!1:b.dc;b=void 0===b.Sb?!0:b.Sb;if(null==A("_lact",window)){var c=parseInt(L("LACT"),10);c=isFinite(c)?Date.now()-Math.max(c,0):-1;z("_lact",c,window);z("_fact",c,window);-1==c&&vn();pn(document,"keydown",vn);pn(document,"keyup",vn);pn(document,"mousedown",vn);pn(document,"mouseup",vn);a?pn(window,"touchmove",function(){wn("touchmove",200)},{passive:!0}):(pn(window,"resize",function(){wn("resize",200)}),b&&pn(window,"scroll",function(){wn("scroll",200)}));
new sn(function(){wn("mouse",100)});
pn(document,"touchstart",vn,{passive:!0});pn(document,"touchend",vn,{passive:!0})}}
function wn(a,b){tn[a]||(tn[a]=!0,af.M(function(){vn();tn[a]=!1},b))}
function vn(){null==A("_lact",window)&&un();var a=Date.now();z("_lact",a,window);-1==A("_fact",window)&&z("_fact",a,window);(a=A("ytglobal.ytUtilActivityCallback_"))&&a()}
function xn(){var a=A("_lact",window);return null==a?-1:Math.max(Date.now()-a,0)}
;var yn=y.ytPubsubPubsubInstance||new K,zn=y.ytPubsubPubsubSubscribedKeys||{},An=y.ytPubsubPubsubTopicToKeys||{},Bn=y.ytPubsubPubsubIsSynchronous||{};function Cn(a,b){var c=Dn();if(c&&b){var d=c.subscribe(a,function(){var e=arguments;var f=function(){zn[d]&&b.apply&&"function"==typeof b.apply&&b.apply(window,e)};
try{Bn[a]?f():oi(f,0)}catch(g){Vh(g)}},void 0);
zn[d]=!0;An[a]||(An[a]=[]);An[a].push(d);return d}return 0}
function En(a){var b=Dn();b&&("number"===typeof a?a=[a]:"string"===typeof a&&(a=[parseInt(a,10)]),eb(a,function(c){b.unsubscribeByKey(c);delete zn[c]}))}
function Fn(a,b){var c=Dn();c&&c.publish.apply(c,arguments)}
function Gn(a){var b=Dn();if(b)if(b.clear(a),a)Hn(a);else for(var c in An)Hn(c)}
function Dn(){return y.ytPubsubPubsubInstance}
function Hn(a){An[a]&&(a=An[a],eb(a,function(b){zn[b]&&delete zn[b]}),a.length=0)}
K.prototype.subscribe=K.prototype.subscribe;K.prototype.unsubscribeByKey=K.prototype.ra;K.prototype.publish=K.prototype.ha;K.prototype.clear=K.prototype.clear;z("ytPubsubPubsubInstance",yn);z("ytPubsubPubsubTopicToKeys",An);z("ytPubsubPubsubIsSynchronous",Bn);z("ytPubsubPubsubSubscribedKeys",zn);var Yn=Ph("initial_gel_batch_timeout",2E3),xo=Math.pow(2,16)-1,yo=void 0;function zo(){this.l=this.i=this.j=0}
var Ao=new zo,Bo=new zo,Co=!0,Do=y.ytLoggingTransportGELQueue_||new Map;z("ytLoggingTransportGELQueue_",Do);var Eo=y.ytLoggingTransportGELProtoQueue_||new Map;z("ytLoggingTransportGELProtoQueue_",Eo);var Fo=y.ytLoggingTransportTokensToCttTargetIds_||{};z("ytLoggingTransportTokensToCttTargetIds_",Fo);var Go=y.ytLoggingTransportTokensToJspbCttTargetIds_||{};z("ytLoggingTransportTokensToJspbCttTargetIds_",Go);
function Ho(a,b){if("log_event"===a.endpoint){var c=Io(a),d=Do.get(c)||[];Do.set(c,d);d.push(a.payload);Jo(b,d,c)}}
function Ko(a,b){if("log_event"===a.endpoint){var c=Io(a,!0),d=Eo.get(c)||[];Eo.set(c,d);a=a.payload.toJSON();d.push(a);Jo(b,d,c,!0)}}
function Jo(a,b,c,d){d=void 0===d?!1:d;a&&(yo=new a);a=Ph("tvhtml5_logging_max_batch")||Ph("web_logging_max_batch")||100;var e=P(),f=d?Bo.l:Ao.l;b.length>=a?Lo({writeThenSend:!0},M("flush_only_full_queue")?c:void 0,d):10<=e-f&&(Mo(d),d?Bo.l=e:Ao.l=e)}
function No(a,b){if("log_event"===a.endpoint){var c=Io(a),d=new Map;d.set(c,[a.payload]);b&&(yo=new b);return new Vf(function(e,f){yo&&yo.isReady()?Oo(d,e,f,{bypassNetworkless:!0},!0):e()})}}
function Po(a,b){if("log_event"===a.endpoint){var c=Io(a,!0),d=new Map;d.set(c,[a.payload.toJSON()]);b&&(yo=new b);return new Vf(function(e){yo&&yo.isReady()?Qo(d,e,{bypassNetworkless:!0},!0):e()})}}
function Io(a,b){var c="";if(a.sa)c="visitorOnlyApprovedKey";else if(a.cttAuthInfo){if(void 0===b?0:b){b=a.cttAuthInfo.token;c=a.cttAuthInfo;var d=new Dh;c.videoId?d.setVideoId(c.videoId):c.playlistId&&ld(d,2,Eh,c.playlistId);Go[b]=d}else b=a.cttAuthInfo,c={},b.videoId?c.videoId=b.videoId:b.playlistId&&(c.playlistId=b.playlistId),Fo[a.cttAuthInfo.token]=c;c=a.cttAuthInfo.token}return c}
function Lo(a,b,c){a=void 0===a?{}:a;c=void 0===c?!1:c;new Vf(function(d,e){c?(pi(Bo.j),pi(Bo.i),Bo.i=0):(pi(Ao.j),pi(Ao.i),Ao.i=0);if(yo&&yo.isReady())if(void 0!==b)if(c){e=new Map;var f=Eo.get(b)||[];e.set(b,f);Qo(e,d,a);Eo.delete(b)}else{f=new Map;var g=Do.get(b)||[];f.set(b,g);Oo(f,d,e,a);Do.delete(b)}else c?(Qo(Eo,d,a),Eo.clear()):(Oo(Do,d,e,a),Do.clear());else Mo(c),d()})}
function Mo(a){a=void 0===a?!1:a;if(M("web_gel_timeout_cap")&&(!a&&!Ao.i||a&&!Bo.i)){var b=oi(function(){Lo({writeThenSend:!0},void 0,a)},6E4);
a?Bo.i=b:Ao.i=b}pi(a?Bo.j:Ao.j);b=L("LOGGING_BATCH_TIMEOUT",Ph("web_gel_debounce_ms",1E4));M("shorten_initial_gel_batch_timeout")&&Co&&(b=Yn);b=oi(function(){Lo({writeThenSend:!0},void 0,a)},b);
a?Bo.j=b:Ao.j=b}
function Oo(a,b,c,d,e){var f=yo;d=void 0===d?{}:d;var g=Math.round(P()),h=a.size;a=q(a);for(var k=a.next();!k.done;k=a.next()){var m=q(k.value);k=m.next().value;var p=m.next().value;m=k;k=vb({context:rj(f.config_||qj())});k.events=p;(p=Fo[m])&&Ro(k,m,p);delete Fo[m];m="visitorOnlyApprovedKey"===m;So(k,g,m);To(d);p=function(){h--;h||b()};
var u=function(){h--;h||b()};
try{Gj(f,"log_event",k,Uo(d,m,p,u,e)),Co=!1}catch(x){Vh(x),c()}}}
function Qo(a,b,c,d){var e=yo;c=void 0===c?{}:c;var f=Math.round(P()),g=a.size;a=q(a);for(var h=a.next();!h.done;h=a.next()){var k=q(h.value);h=k.next().value;var m=k=k.next().value;k=new Fh;var p=wj(e.config_||qj());D(k,1,p);m=Vo(m);for(p=0;p<m.length;p++)qd(k,3,zh,m[p]);(m=Go[h])&&Wo(k,h,m);delete Go[h];h="visitorOnlyApprovedKey"===h;Xo(k,f,h);To(c);a:{Uc=!0;try{var u=JSON.stringify(k.toJSON(),vd);break a}finally{Uc=!1}u=void 0}k=u;h=Uo(c,h,function(){g--;g||b()},function(){g--;
g||b()},d);
h.headers={"Content-Type":"application/json+protobuf"};h.postBodyFormat="JSPB";h.postBody=k;Gj(e,"log_event","",h);Co=!1}}
function To(a){M("always_send_and_write")&&(a.writeThenSend=!1)}
function Uo(a,b,c,d,e){return{retry:!0,onSuccess:c,onError:d,zb:a,sa:b,gp:!!e,headers:{},postBodyFormat:"",postBody:""}}
function So(a,b,c){a.requestTimeMs=String(b);M("unsplit_gel_payloads_in_logs")&&(a.unsplitGelPayloadsInLogs=!0);!c&&(b=L("EVENT_ID"))&&(c=Yo(),a.serializedClientEventId={serializedEventId:b,clientCounter:String(c)})}
function Xo(a,b,c){C(a,2,b);if(!c&&(b=L("EVENT_ID"))){c=Yo();var d=new Ch;C(d,1,b);C(d,2,c);D(a,5,d)}}
function Yo(){var a=L("BATCH_CLIENT_COUNTER")||0;a||(a=Math.floor(Math.random()*xo/2));a++;a>xo&&(a=1);Mh("BATCH_CLIENT_COUNTER",a);return a}
function Ro(a,b,c){if(c.videoId)var d="VIDEO";else if(c.playlistId)d="PLAYLIST";else return;a.credentialTransferTokenTargetId=c;a.context=a.context||{};a.context.user=a.context.user||{};a.context.user.credentialTransferTokens=[{token:b,scope:d}]}
function Wo(a,b,c){if(jd(c,1===md(c,Eh)?1:-1))var d=1;else if(c.getPlaylistId())d=2;else return;D(a,4,c);a=nd(a,Og,1)||new Og;c=nd(a,Mg,3)||new Mg;var e=new Lg;e.setToken(b);C(e,1,d);qd(c,12,Lg,e);D(a,3,c)}
function Vo(a){for(var b=[],c=0;c<a.length;c++)try{b.push(new zh(a[c]))}catch(d){Vh(new Q("Transport failed to deserialize "+String(a[c])))}return b}
;var Zo=y.ytLoggingGelSequenceIdObj_||{};z("ytLoggingGelSequenceIdObj_",Zo);
function $o(a,b,c,d){d=void 0===d?{}:d;if(M("lr_drop_other_and_business_payloads")){if(fn[a]||en[a])return}else if(M("lr_drop_other_payloads")&&fn[a])return;var e={},f=Math.round(d.timestamp||P());e.eventTimeMs=f<Number.MAX_SAFE_INTEGER?f:0;e[a]=b;a=xn();e.context={lastActivityMs:String(d.timestamp||!isFinite(a)?-1:a)};M("log_sequence_info_on_gel_web")&&d.V&&(a=e.context,b=d.V,b={index:ap(b),groupKey:b},a.sequence=b,d.rb&&delete Zo[d.V]);(d.mc?No:Ho)({endpoint:"log_event",payload:e,cttAuthInfo:d.cttAuthInfo,
sa:d.sa},c)}
function bp(a){Lo(void 0,void 0,void 0===a?!1:a)}
function ap(a){Zo[a]=a in Zo?Zo[a]+1:0;return Zo[a]}
;function Yj(a,b,c){c=void 0===c?{}:c;var d=dn;L("ytLoggingEventsDefaultDisabled",!1)&&dn==dn&&(d=null);$o(a,b,d,c)}
;var cp=[{cb:function(a){return"Cannot read property '"+a.key+"'"},
Oa:{Error:[{regexp:/(Permission denied) to access property "([^']+)"/,groups:["reason","key"]}],TypeError:[{regexp:/Cannot read property '([^']+)' of (null|undefined)/,groups:["key","value"]},{regexp:/\u65e0\u6cd5\u83b7\u53d6\u672a\u5b9a\u4e49\u6216 (null|undefined) \u5f15\u7528\u7684\u5c5e\u6027\u201c([^\u201d]+)\u201d/,groups:["value","key"]},{regexp:/\uc815\uc758\ub418\uc9c0 \uc54a\uc74c \ub610\ub294 (null|undefined) \ucc38\uc870\uc778 '([^']+)' \uc18d\uc131\uc744 \uac00\uc838\uc62c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4./,
groups:["value","key"]},{regexp:/No se puede obtener la propiedad '([^']+)' de referencia nula o sin definir/,groups:["key"]},{regexp:/Unable to get property '([^']+)' of (undefined or null) reference/,groups:["key","value"]},{regexp:/(null) is not an object \(evaluating '(?:([^.]+)\.)?([^']+)'\)/,groups:["value","base","key"]}]}},{cb:function(a){return"Cannot call '"+a.key+"'"},
Oa:{TypeError:[{regexp:/(?:([^ ]+)?\.)?([^ ]+) is not a function/,groups:["base","key"]},{regexp:/([^ ]+) called on (null or undefined)/,groups:["key","value"]},{regexp:/Object (.*) has no method '([^ ]+)'/,groups:["base","key"]},{regexp:/Object doesn't support property or method '([^ ]+)'/,groups:["key"]},{regexp:/\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306f '([^']+)' \u30d7\u30ed\u30d1\u30c6\u30a3\u307e\u305f\u306f\u30e1\u30bd\u30c3\u30c9\u3092\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u307e\u305b\u3093/,
groups:["key"]},{regexp:/\uac1c\uccb4\uac00 '([^']+)' \uc18d\uc131\uc774\ub098 \uba54\uc11c\ub4dc\ub97c \uc9c0\uc6d0\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4./,groups:["key"]}]}},{cb:function(a){return a.key+" is not defined"},
Oa:{ReferenceError:[{regexp:/(.*) is not defined/,groups:["key"]},{regexp:/Can't find variable: (.*)/,groups:["key"]}]}}];var ep={fa:[],ea:[{ia:dp,weight:500}]};function dp(a){if("JavaException"===a.name)return!0;a=a.stack;return a.includes("chrome://")||a.includes("chrome-extension://")||a.includes("moz-extension://")}
;function fp(){this.ea=[];this.fa=[]}
var gp;function hp(){if(!gp){var a=gp=new fp;a.fa.length=0;a.ea.length=0;ep.fa&&a.fa.push.apply(a.fa,ep.fa);ep.ea&&a.ea.push.apply(a.ea,ep.ea)}return gp}
;var ip=new K;function jp(a){function b(){return a.charCodeAt(d++)}
var c=a.length,d=0;do{var e=kp(b);if(Infinity===e)break;var f=e>>3;switch(e&7){case 0:e=kp(b);if(2===f)return e;break;case 1:if(2===f)return;d+=8;break;case 2:e=kp(b);if(2===f)return a.substr(d,e);d+=e;break;case 5:if(2===f)return;d+=4;break;default:return}}while(d<c)}
function kp(a){var b=a(),c=b&127;if(128>b)return c;b=a();c|=(b&127)<<7;if(128>b)return c;b=a();c|=(b&127)<<14;if(128>b)return c;b=a();return 128>b?c|(b&127)<<21:Infinity}
;function lp(a,b,c,d){if(a)if(Array.isArray(a)){var e=d;for(d=0;d<a.length&&!(a[d]&&(e+=mp(d,a[d],b,c),500<e));d++);d=e}else if("object"===typeof a)for(e in a){if(a[e]){var f=a[e];var g=b;var h=c;g="string"!==typeof f||"clickTrackingParams"!==e&&"trackingParams"!==e?0:(f=jp(atob(f.replace(/-/g,"+").replace(/_/g,"/"))))?mp(e+".ve",f,g,h):0;d+=g;d+=mp(e,a[e],b,c);if(500<d)break}}else c[b]=np(a),d+=c[b].length;else c[b]=np(a),d+=c[b].length;return d}
function mp(a,b,c,d){c+="."+a;a=np(b);d[c]=a;return c.length+a.length}
function np(a){try{return("string"===typeof a?a:String(JSON.stringify(a))).substr(0,500)}catch(b){return"unable to serialize "+typeof a+" ("+b.message+")"}}
;var op=y.ytLoggingGelSequenceIdObj_||{};z("ytLoggingGelSequenceIdObj_",op);function pp(a,b,c){c=void 0===c?{}:c;var d=Math.round(c.timestamp||P());C(a,1,d<Number.MAX_SAFE_INTEGER?d:0);var e=xn();d=new yh;C(d,1,c.timestamp||!isFinite(e)?-1:e);if(M("log_sequence_info_on_gel_web")&&c.V){e=c.V;var f=ap(e),g=new xh;C(g,2,f);C(g,1,e);D(d,3,g);c.rb&&delete op[c.V]}D(a,33,d);(c.mc?Po:Ko)({endpoint:"log_event",payload:a,cttAuthInfo:c.cttAuthInfo,sa:c.sa},b)}
;function qp(a,b){b=void 0===b?{}:b;var c=!1;L("ytLoggingEventsDefaultDisabled",!1)&&dn===dn&&(c=!0);pp(a,c?null:dn,b)}
;function rp(a,b,c){var d=new zh;pd(d,73,Ah,a);c?pp(d,c,b):qp(d,b)}
function sp(a,b,c){var d=new zh;pd(d,78,Ah,a);c?pp(d,c,b):qp(d,b)}
function tp(a,b,c){var d=new zh;pd(d,208,Ah,a);c?pp(d,c,b):qp(d,b)}
function up(a,b,c){var d=new zh;pd(d,156,Ah,a);c?pp(d,c,b):qp(d,b)}
function vp(a,b,c){var d=new zh;pd(d,215,Ah,a);c?pp(d,c,b):qp(d,b)}
function wp(a,b,c){var d=new zh;pd(d,111,Ah,a);c?pp(d,c,b):qp(d,b)}
;var xp=new Set,yp=0,zp=0,Ap=0,Bp=[],Cp=["PhantomJS","Googlebot","TO STOP THIS SECURITY SCAN go/scan"];function Xj(a){Dp(a)}
function Ep(a){Dp(a,"WARNING")}
function Dp(a,b,c,d,e,f){f=void 0===f?{}:f;f.name=c||L("INNERTUBE_CONTEXT_CLIENT_NAME",1);f.version=d||L("INNERTUBE_CONTEXT_CLIENT_VERSION");var g=f||{},h=void 0===b?"ERROR":b;h=void 0===h?"ERROR":h;if(a){a.hasOwnProperty("level")&&a.level&&(h=a.level);if(M("console_log_js_exceptions")){var k=[];k.push("Name: "+a.name);k.push("Message: "+a.message);a.hasOwnProperty("params")&&k.push("Error Params: "+JSON.stringify(a.params));a.hasOwnProperty("args")&&k.push("Error args: "+JSON.stringify(a.args));
k.push("File name: "+a.fileName);k.push("Stacktrace: "+a.stack);window.console.log(k.join("\n"),a)}if(!(5<=yp)){var m=Bp,p=se(a),u=p.message||"Unknown Error",x=p.name||"UnknownError",v=p.stack||a.j||"Not available";if(v.startsWith(x+": "+u)){var E=v.split("\n");E.shift();v=E.join("\n")}var G=p.lineNumber||"Not available",H=p.fileName||"Not available",R=v,N=0;if(a.hasOwnProperty("args")&&a.args&&a.args.length)for(var S=0;S<a.args.length&&!(N=lp(a.args[S],"params."+S,g,N),500<=N);S++);else if(a.hasOwnProperty("params")&&
a.params){var ja=a.params;if("object"===typeof a.params)for(var O in ja){if(ja[O]){var Ba="params."+O,Na=np(ja[O]);g[Ba]=Na;N+=Ba.length+Na.length;if(500<N)break}}else g.params=np(ja)}if(m.length)for(var va=0;va<m.length&&!(N=lp(m[va],"params.context."+va,g,N),500<=N);va++);navigator.vendor&&!g.hasOwnProperty("vendor")&&(g["device.vendor"]=navigator.vendor);var F={message:u,name:x,lineNumber:G,fileName:H,stack:R,params:g,sampleWeight:1},Ca=Number(a.columnNumber);isNaN(Ca)||(F.lineNumber=F.lineNumber+
":"+Ca);if("IGNORED"===a.level)var ua=0;else a:{for(var In=hp(),Jn=q(In.fa),wi=Jn.next();!wi.done;wi=Jn.next()){var Kn=wi.value;if(F.message&&F.message.match(Kn.np)){ua=Kn.weight;break a}}for(var Ln=q(In.ea),xi=Ln.next();!xi.done;xi=Ln.next()){var Mn=xi.value;if(Mn.ia(F)){ua=Mn.weight;break a}}ua=1}F.sampleWeight=ua;for(var Nn=q(cp),yi=Nn.next();!yi.done;yi=Nn.next()){var zi=yi.value;if(zi.Oa[F.name])for(var On=q(zi.Oa[F.name]),Ai=On.next();!Ai.done;Ai=On.next()){var Pn=Ai.value,Of=F.message.match(Pn.regexp);
if(Of){F.params["params.error.original"]=Of[0];for(var Bi=Pn.groups,Qn={},Vc=0;Vc<Bi.length;Vc++)Qn[Bi[Vc]]=Of[Vc+1],F.params["params.error."+Bi[Vc]]=Of[Vc+1];F.message=zi.cb(Qn);break}}}F.params||(F.params={});var Rn=hp();F.params["params.errorServiceSignature"]="msg="+Rn.fa.length+"&cb="+Rn.ea.length;F.params["params.serviceWorker"]="false";y.document&&y.document.querySelectorAll&&(F.params["params.fscripts"]=String(document.querySelectorAll("script:not([nonce])").length));Cb("sample").constructor!==
Ab&&(F.params["params.fconst"]="true");window.yterr&&"function"===typeof window.yterr&&window.yterr(F);if(0!==F.sampleWeight&&!xp.has(F.message)){if("ERROR"===h){ip.ha("handleError",F);if(M("record_app_crashed_web")&&0===Ap&&1===F.sampleWeight)if(Ap++,M("errors_via_jspb")){var Ci=new ah;C(Ci,1,1);if(!M("report_client_error_with_app_crash_ks")){var Sn=new Wg;C(Sn,1,F.message);var Tn=new Xg;D(Tn,3,Sn);var Un=new Yg;D(Un,5,Tn);var Vn=new Zg;D(Vn,9,Un);D(Ci,4,Vn)}var Wn=new zh;pd(Wn,20,Ah,Ci);qp(Wn)}else{var Xn=
{appCrashType:"APP_CRASH_TYPE_BREAKPAD"};M("report_client_error_with_app_crash_ks")||(Xn.systemHealth={crashData:{clientError:{logMessage:{message:F.message}}}});Yj("appCrashed",Xn)}zp++}else"WARNING"===h&&ip.ha("handleWarning",F);if(M("kevlar_gel_error_routing"))a:{var Ud=h;if(M("errors_via_jspb")){if(Fp())var Zn=void 0;else{var Wc=new Tg;C(Wc,1,F.stack);F.fileName&&C(Wc,4,F.fileName);var rb=F.lineNumber&&F.lineNumber.split?F.lineNumber.split(":"):[];0!==rb.length&&(1!==rb.length||isNaN(Number(rb[0]))?
2!==rb.length||isNaN(Number(rb[0]))||isNaN(Number(rb[1]))||(C(Wc,2,Number(rb[0])),C(Wc,3,Number(rb[1]))):C(Wc,2,Number(rb[0])));var kc=new Wg;C(kc,1,F.message);C(kc,3,F.name);C(kc,6,F.sampleWeight);"ERROR"===Ud?C(kc,2,2):"WARNING"===Ud?C(kc,2,1):C(kc,2,0);var Di=new Ug;C(Di,1,!0);pd(Di,3,Vg,Wc);var Lb=new Qg;C(Lb,3,window.location.href);for(var $n=L("FEXP_EXPERIMENTS",[]),Ei=0;Ei<$n.length;Ei++){var ot=$n[Ei];Zc(Lb);kd(Lb,5).push(ot)}var Fi=L("LATEST_ECATCHER_SERVICE_TRACKING_PARAMS");if(!Nh()&&Fi)for(var ao=
q(Object.keys(Fi)),lc=ao.next();!lc.done;lc=ao.next()){var bo=lc.value,Gi=new Sg;C(Gi,1,bo);Gi.setValue(String(Fi[bo]));qd(Lb,4,Sg,Gi)}var Hi=F.params;if(Hi){var co=q(Object.keys(Hi));for(lc=co.next();!lc.done;lc=co.next()){var eo=lc.value,Ii=new Sg;C(Ii,1,"client."+eo);Ii.setValue(String(Hi[eo]));qd(Lb,4,Sg,Ii)}}var fo=L("SERVER_NAME"),go=L("SERVER_VERSION");if(fo&&go){var Ji=new Sg;C(Ji,1,"server.name");Ji.setValue(fo);qd(Lb,4,Sg,Ji);var Ki=new Sg;C(Ki,1,"server.version");Ki.setValue(go);qd(Lb,
4,Sg,Ki)}var Pf=new Xg;D(Pf,1,Lb);D(Pf,2,Di);D(Pf,3,kc);Zn=Pf}var ho=Zn;if(!ho)break a;var io=new zh;pd(io,163,Ah,ho);qp(io)}else{if(Fp())var jo=void 0;else{var Vd={stackTrace:F.stack};F.fileName&&(Vd.filename=F.fileName);var sb=F.lineNumber&&F.lineNumber.split?F.lineNumber.split(":"):[];0!==sb.length&&(1!==sb.length||isNaN(Number(sb[0]))?2!==sb.length||isNaN(Number(sb[0]))||isNaN(Number(sb[1]))||(Vd.lineNumber=Number(sb[0]),Vd.columnNumber=Number(sb[1])):Vd.lineNumber=Number(sb[0]));var Li={level:"ERROR_LEVEL_UNKNOWN",
message:F.message,errorClassName:F.name,sampleWeight:F.sampleWeight};"ERROR"===Ud?Li.level="ERROR_LEVEL_ERROR":"WARNING"===Ud&&(Li.level="ERROR_LEVEL_WARNNING");var pt={isObfuscated:!0,browserStackInfo:Vd},Xc={pageUrl:window.location.href,kvPairs:[]};L("FEXP_EXPERIMENTS")&&(Xc.experimentIds=L("FEXP_EXPERIMENTS"));var Mi=L("LATEST_ECATCHER_SERVICE_TRACKING_PARAMS");if(!Nh()&&Mi)for(var ko=q(Object.keys(Mi)),mc=ko.next();!mc.done;mc=ko.next()){var lo=mc.value;Xc.kvPairs.push({key:lo,value:String(Mi[lo])})}var Ni=
F.params;if(Ni){var mo=q(Object.keys(Ni));for(mc=mo.next();!mc.done;mc=mo.next()){var no=mc.value;Xc.kvPairs.push({key:"client."+no,value:String(Ni[no])})}}var oo=L("SERVER_NAME"),po=L("SERVER_VERSION");oo&&po&&(Xc.kvPairs.push({key:"server.name",value:oo}),Xc.kvPairs.push({key:"server.version",value:po}));jo={errorMetadata:Xc,stackTrace:pt,logMessage:Li}}var qo=jo;if(!qo)break a;Yj("clientError",qo)}if("ERROR"===Ud||M("errors_flush_gel_always_killswitch"))M("web_fp_via_jspb")&&bp(!0),bp()}if(!M("suppress_error_204_logging")){var Wd=
F.params||{},Mb={urlParams:{a:"logerror",t:"jserror",type:F.name,msg:F.message.substr(0,250),line:F.lineNumber,level:h,"client.name":Wd.name},postParams:{url:L("PAGE_NAME",window.location.href),file:F.fileName},method:"POST"};Wd.version&&(Mb["client.version"]=Wd.version);if(Mb.postParams){F.stack&&(Mb.postParams.stack=F.stack);for(var ro=q(Object.keys(Wd)),Oi=ro.next();!Oi.done;Oi=ro.next()){var so=Oi.value;Mb.postParams["client."+so]=Wd[so]}var Pi=L("LATEST_ECATCHER_SERVICE_TRACKING_PARAMS");if(Pi)for(var to=
q(Object.keys(Pi)),Qi=to.next();!Qi.done;Qi=to.next()){var uo=Qi.value;Mb.postParams[uo]=Pi[uo]}var vo=L("SERVER_NAME"),wo=L("SERVER_VERSION");vo&&wo&&(Mb.postParams["server.name"]=vo,Mb.postParams["server.version"]=wo)}vi(L("ECATCHER_REPORT_HOST","")+"/error_204",Mb)}try{xp.add(F.message)}catch(Ou){}yp++}}}}
function Fp(){for(var a=q(Cp),b=a.next();!b.done;b=a.next())if(Pj(b.value.toLowerCase()))return!0;return!1}
function Gp(a){var b=Ja.apply(1,arguments);a.args||(a.args=[]);a.args.push.apply(a.args,fa(b))}
;function Hp(){this.register=new Map}
function Ip(a){a=q(a.register.values());for(var b=a.next();!b.done;b=a.next())b.value.sp("ABORTED")}
Hp.prototype.clear=function(){Ip(this);this.register.clear()};
var Jp=new Hp;var Kp=Date.now().toString();
function Lp(){a:{if(window.crypto&&window.crypto.getRandomValues)try{var a=Array(16),b=new Uint8Array(16);window.crypto.getRandomValues(b);for(var c=0;c<a.length;c++)a[c]=b[c];var d=a;break a}catch(e){}d=Array(16);for(a=0;16>a;a++){b=Date.now();for(c=0;c<b%23;c++)d[a]=Math.random();d[a]=Math.floor(256*Math.random())}if(Kp)for(a=1,b=0;b<Kp.length;b++)d[a%16]=d[a%16]^d[(a-1)%16]/4^Kp.charCodeAt(b),a++}a=[];for(b=0;b<d.length;b++)a.push("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".charAt(d[b]&63));
return a.join("")}
;var Mp=y.ytLoggingDocDocumentNonce_;Mp||(Mp=Lp(),z("ytLoggingDocDocumentNonce_",Mp));var Np=Mp;var Op={Dg:0,rd:1,Bd:2,rk:3,Fg:4,Co:5,kl:6,Mm:7,km:8,0:"DEFAULT",1:"CHAT",2:"CONVERSATIONS",3:"MINIPLAYER",4:"DIALOG",5:"VOZ",6:"MUSIC_WATCH_TABS",7:"SHARE",8:"PUSH_NOTIFICATIONS"};function Pp(a){this.i=a}
function Qp(a){return new Pp({trackingParams:a})}
Pp.prototype.getAsJson=function(){var a={};void 0!==this.i.trackingParams?a.trackingParams=this.i.trackingParams:(a.veType=this.i.veType,void 0!==this.i.veCounter&&(a.veCounter=this.i.veCounter),void 0!==this.i.elementIndex&&(a.elementIndex=this.i.elementIndex));void 0!==this.i.dataElement&&(a.dataElement=this.i.dataElement.getAsJson());void 0!==this.i.youtubeData&&(a.youtubeData=this.i.youtubeData);return a};
Pp.prototype.getAsJspb=function(){var a=new ch;void 0!==this.i.trackingParams?C(a,1,this.i.trackingParams):(void 0!==this.i.veType&&C(a,2,this.i.veType),void 0!==this.i.veCounter&&C(a,6,this.i.veCounter),void 0!==this.i.elementIndex&&C(a,3,this.i.elementIndex));if(void 0!==this.i.dataElement){var b=this.i.dataElement.getAsJspb();D(a,7,b)}void 0!==this.i.youtubeData&&D(a,8,this.i.jspbYoutubeData);return a};
Pp.prototype.toString=function(){return JSON.stringify(this.getAsJson())};
Pp.prototype.isClientVe=function(){return!this.i.trackingParams&&!!this.i.veType};function Rp(a){a=void 0===a?0:a;return 0===a?"client-screen-nonce":"client-screen-nonce."+a}
function Sp(a){a=void 0===a?0:a;return 0===a?"ROOT_VE_TYPE":"ROOT_VE_TYPE."+a}
function Tp(a){return L(Sp(void 0===a?0:a))}
z("yt_logging_screen.getRootVeType",Tp);function Up(a){return(a=Tp(void 0===a?0:a))?new Pp({veType:a,youtubeData:void 0,jspbYoutubeData:void 0}):null}
function Vp(){var a=L("csn-to-ctt-auth-info");a||(a={},Mh("csn-to-ctt-auth-info",a));return a}
function Wp(a){a=L(Rp(void 0===a?0:a));if(!a&&!L("USE_CSN_FALLBACK",!0))return null;a||(a="UNDEFINED_CSN");return a?a:null}
z("yt_logging_screen.getCurrentCsn",Wp);function Xp(a,b,c){var d=Vp();(c=Wp(c))&&delete d[c];b&&(d[a]=b)}
function Yp(a){return Vp()[a]}
z("yt_logging_screen.getCttAuthInfo",Yp);
function Zp(a,b,c,d){c=void 0===c?0:c;if(a!==L(Rp(c))||b!==L(Sp(c)))Xp(a,d,c),Mh(Rp(c),a),Mh(Sp(c),b),b=function(){setTimeout(function(){if(a)if(M("web_time_via_jspb")){var e=new dh;C(e,1,Np);C(e,2,a);M("use_default_heartbeat_client")?wp(e):wp(e,void 0,dn)}else e={clientDocumentNonce:Np,clientScreenNonce:a},M("use_default_heartbeat_client")?Yj("foregroundHeartbeatScreenAssociated",e):$o("foregroundHeartbeatScreenAssociated",e,dn)},0)},"requestAnimationFrame"in window?window.requestAnimationFrame(b):
b()}
z("yt_logging_screen.setCurrentScreen",Zp);var $p=window.yt&&window.yt.msgs_||window.ytcfg&&window.ytcfg.msgs||{};z("yt.msgs_",$p);function aq(a){Hh($p,arguments)}
;var bq={qd:3611,Cc:27686,Dc:85013,Ec:23462,Gc:42016,Hc:62407,Ic:26926,Fc:43781,Jc:51236,Kc:79148,Lc:50160,Mc:77504,Yc:87907,Zc:18630,bd:54445,cd:80935,dd:152172,ed:105675,fd:150723,gd:37521,hd:147285,jd:47786,kd:98349,ld:123695,md:6827,nd:29434,od:7282,pd:124448,td:32276,sd:76278,ud:147868,vd:147869,wd:93911,xd:106531,yd:27259,zd:27262,Ad:27263,Cd:21759,Dd:27107,Ed:62936,Fd:49568,Gd:38408,Hd:80637,Id:68727,Jd:68728,Kd:80353,Ld:80356,Md:74610,Nd:45707,Od:83962,Pd:83970,Qd:46713,Rd:89711,Sd:74612,Td:93265,
Ud:74611,Vd:131380,Xd:128979,Yd:139311,Zd:128978,Wd:131391,ae:105350,ce:139312,de:134800,be:131392,ge:113533,he:93252,ie:99357,ke:94521,le:114252,me:113532,ne:94522,je:94583,oe:88E3,pe:139580,qe:93253,re:93254,se:94387,te:94388,ue:93255,we:97424,ee:72502,xe:110111,ye:76019,Ae:117092,Be:117093,ze:89431,Ce:110466,De:77240,Ee:60508,Fe:148123,Ge:148124,He:137401,Ie:137402,Je:137046,Ke:73393,Le:113534,Me:92098,Ne:131381,Oe:84517,Pe:83759,Qe:80357,Re:86113,Se:72598,Te:72733,Ue:107349,Ve:124275,We:118203,
Xe:133275,Ye:133274,Ze:133272,af:133273,bf:133276,cf:144507,df:143247,ef:143248,ff:143249,gf:143250,hf:143251,jf:144401,lf:117431,kf:133797,mf:128572,nf:133405,pf:117429,qf:117430,rf:117432,sf:120080,tf:117259,uf:121692,vf:145656,wf:145655,xf:145653,yf:145654,zf:145657,Af:132972,Bf:133051,Cf:133658,Df:132971,Ef:97615,Gf:143359,Ff:143356,If:143361,Hf:143358,Kf:143360,Jf:143357,Lf:142303,Mf:143353,Nf:143354,Of:144479,Pf:143355,Qf:31402,Sf:133624,Tf:146477,Uf:133623,Vf:133622,Rf:133621,Wf:84774,Xf:95117,
Yf:150497,Zf:98930,ag:98931,cg:98932,dg:43347,eg:129889,fg:149123,gg:45474,hg:100352,ig:84758,jg:98443,kg:117985,lg:74613,mg:74614,ng:64502,og:136032,pg:74615,qg:74616,rg:122224,sg:74617,tg:77820,ug:74618,vg:93278,wg:93274,xg:93275,yg:93276,zg:22110,Ag:29433,Bg:133798,Cg:132295,Eg:120541,Gg:82047,Hg:113550,Ig:75836,Jg:75837,Kg:42352,Lg:84512,Mg:76065,Ng:75989,Sg:16623,Tg:32594,Ug:27240,Vg:32633,Wg:74858,Yg:3945,Xg:16989,Zg:45520,ah:25488,bh:25492,dh:25494,eh:55760,fh:14057,gh:18451,hh:57204,ih:57203,
jh:17897,kh:57205,lh:18198,mh:17898,nh:17909,oh:43980,ph:46220,qh:11721,rh:147994,sh:49954,th:96369,uh:3854,vh:151633,wh:56251,xh:25624,Oh:16906,Ph:99999,Qh:68172,Rh:27068,Sh:47973,Th:72773,Uh:26970,Vh:26971,Wh:96805,Xh:17752,Yh:73233,Zh:109512,ai:22256,bi:14115,ci:22696,di:89278,fi:89277,gi:109513,hi:43278,ii:43459,ji:43464,ki:89279,li:43717,mi:55764,ni:22255,oi:147912,ri:89281,si:40963,ti:43277,vi:43442,wi:91824,xi:120137,yi:96367,zi:36850,Ai:72694,Bi:37414,Ci:36851,Ei:124863,Di:121343,Fi:73491,
Gi:54473,Hi:43375,Ii:46674,Ji:143815,Ki:139095,Li:144402,Mi:149968,Ni:149969,Oi:32473,Pi:72901,Qi:72906,Ri:50947,Si:50612,Ti:50613,Ui:50942,Vi:84938,Wi:84943,Xi:84939,Yi:84941,Zi:84944,aj:84940,bj:84942,cj:35585,dj:51926,ej:79983,fj:63238,gj:18921,hj:63241,ij:57893,jj:41182,kj:135732,lj:33424,mj:22207,nj:42993,oj:36229,pj:22206,qj:22205,rj:18993,sj:19001,tj:18990,uj:18991,vj:18997,wj:18725,xj:19003,yj:36874,zj:44763,Aj:33427,Bj:67793,Cj:22182,Dj:37091,Ej:34650,Fj:50617,Gj:47261,Hj:22287,Ij:25144,
Jj:97917,Kj:62397,Lj:150871,Mj:150874,Nj:125598,Oj:137935,Pj:36961,Qj:108035,Rj:27426,Sj:27857,Tj:27846,Uj:27854,Vj:69692,Wj:61411,Xj:39299,Yj:38696,Zj:62520,ak:36382,bk:108701,ck:50663,dk:36387,ek:14908,fk:37533,gk:105443,hk:61635,ik:62274,jk:133818,kk:65702,lk:65703,mk:65701,nk:76256,pk:37671,qk:49953,sk:36216,tk:28237,uk:39553,vk:29222,wk:26107,xk:38050,yk:26108,Ak:120745,zk:26109,Bk:26110,Ck:66881,Dk:28236,Ek:14586,Fk:57929,Gk:74723,Hk:44098,Ik:44099,Lk:23528,Mk:61699,Jk:134104,Kk:134103,Nk:59149,
Ok:101951,Pk:97346,Qk:118051,Rk:95102,Sk:64882,Tk:119505,Uk:63595,Vk:63349,Wk:95101,Xk:75240,Yk:27039,Zk:68823,al:21537,bl:83464,dl:75707,fl:83113,il:101952,jl:101953,ll:79610,ml:125755,nl:24402,ol:24400,pl:32925,ql:57173,rl:122502,sl:145268,ul:138480,vl:64423,wl:64424,xl:33986,yl:100828,zl:129089,Al:21409,El:135155,Fl:135156,Gl:135157,Hl:135158,Il:135159,Jl:135160,Kl:135161,Ll:135162,Ml:135163,Nl:135164,Ol:135165,Pl:135166,Bl:11070,Cl:11074,Dl:17880,Ql:14001,Sl:30709,Tl:30707,Ul:30711,Vl:30710,Wl:30708,
Rl:26984,Xl:146143,Yl:63648,Zl:63649,am:51879,bm:111059,cm:5754,dm:20445,em:151152,gm:130975,fm:130976,hm:110386,im:113746,jm:66557,lm:17310,mm:28631,nm:21589,om:68012,pm:60480,qm:138664,rm:141121,sm:31571,tm:141978,um:150105,vm:150106,wm:150107,xm:150108,ym:76980,zm:41577,Am:45469,Bm:38669,Cm:13768,Dm:13777,Em:141842,Fm:62985,Gm:4724,Hm:59369,Im:43927,Jm:43928,Km:12924,Lm:100355,Om:56219,Pm:27669,Qm:10337,Nm:47896,Rm:122629,Tm:139723,Sm:139722,Um:121258,Vm:107598,Wm:127991,Xm:96639,Ym:107536,Zm:130169,
an:96661,bn:145188,cn:96658,dn:116646,en:121122,fn:96660,gn:127738,hn:127083,jn:147842,kn:104443,ln:96659,mn:147595,nn:106442,pn:134840,qn:63667,rn:63668,sn:63669,tn:130686,un:147036,vn:78314,wn:147799,xn:148649,yn:55761,zn:127098,An:134841,Bn:96368,Cn:67374,Dn:48992,En:146176,Fn:49956,Gn:31961,Hn:26388,In:23811,Jn:5E4,Kn:126250,Ln:96370,Mn:47355,Nn:47356,On:37935,Pn:45521,Qn:21760,Rn:83769,Sn:49977,Tn:49974,Un:93497,Vn:93498,Wn:34325,Xn:140759,Yn:115803,Zn:123707,ao:100081,bo:35309,co:68314,eo:25602,
fo:100339,ho:143516,jo:59018,ko:18248,lo:50625,mo:9729,no:37168,oo:37169,po:21667,qo:16749,ro:18635,so:39305,to:18046,uo:53969,vo:8213,wo:93926,xo:102852,yo:110099,zo:22678,Ao:69076,Bo:137575,Do:139224,Eo:100856,Fo:17736,Go:3832,Ho:147111,Io:55759,Jo:64031,Po:93044,Qo:93045,Ro:34388,So:17657,To:17655,Uo:39579,Vo:39578,Wo:77448,Xo:8196,Yo:11357,Zo:69877,ap:8197,bp:82039};function cq(){var a=ub(dq),b;return bg(new Vf(function(c,d){a.onSuccess=function(e){ni(e)?c(new eq(e)):d(new fq("Request failed, status="+(e&&"status"in e?e.status:-1),"net.badstatus",e))};
a.onError=function(e){d(new fq("Unknown request error","net.unknown",e))};
a.onTimeout=function(e){d(new fq("Request timed out","net.timeout",e))};
b=vi("//googleads.g.doubleclick.net/pagead/id",a)}),function(c){c instanceof cg&&b.abort();
return $f(c)})}
function fq(a,b,c){$a.call(this,a+", errorCode="+b);this.errorCode=b;this.xhr=c;this.name="PromiseAjaxError"}
r(fq,$a);function eq(a){this.xhr=a}
;function gq(){this.j=0;this.i=null}
gq.prototype.then=function(a,b,c){return 1===this.j&&a?(a=a.call(c,this.i))&&"function"===typeof a.then?a:hq(a):2===this.j&&b?(a=b.call(c,this.i))&&"function"===typeof a.then?a:iq(a):this};
gq.prototype.getValue=function(){return this.i};
gq.prototype.$goog_Thenable=!0;function iq(a){var b=new gq;a=void 0===a?null:a;b.j=2;b.i=void 0===a?null:a;return b}
function hq(a){var b=new gq;a=void 0===a?null:a;b.j=1;b.i=void 0===a?null:a;return b}
;function jq(a,b){return{method:void 0===b?"POST":b,mode:ii(a)?"same-origin":"cors",credentials:ii(a)?"same-origin":"include"}}
;function kq(){if(le()||Oj&&Pj("applewebkit")&&!Pj("version")&&(!Pj("safari")||Pj("gsa/"))||wc&&Pj("version/"))return!0;if(M("enable_web_eom_visitor_data"))return L("EOM_VISITOR_DATA")?!1:!0;var a=L("INNERTUBE_CLIENT_NAME");return!a||"WEB"!==a&&"MWEB"!==a?!0:(a=aj("CONSENT"))?a.startsWith("YES+"):!0}
;function lq(a){var b=a.raw_embedded_player_response;if(!b&&(a=a.embedded_player_response))try{b=JSON.parse(a)}catch(d){return"EMBEDDED_PLAYER_MODE_UNKNOWN"}if(b)a:{for(var c in bf)if(bf[c]==b.embeddedPlayerMode){b=bf[c];break a}b="EMBEDDED_PLAYER_MODE_UNKNOWN"}else b="EMBEDDED_PLAYER_MODE_UNKNOWN";return b}
;function mq(a){$a.call(this,a.message||a.description||a.name);this.isMissing=a instanceof nq;this.isTimeout=a instanceof fq&&"net.timeout"==a.errorCode;this.isCanceled=a instanceof cg}
r(mq,$a);mq.prototype.name="BiscottiError";function nq(){$a.call(this,"Biscotti ID is missing from server")}
r(nq,$a);nq.prototype.name="BiscottiMissingError";var dq={format:"RAW",method:"GET",timeout:5E3,withCredentials:!0},oq=null;
function Yh(){if(M("disable_biscotti_fetch_entirely_for_all_web_clients"))return $f(Error("Biscotti id fetching has been disabled entirely."));if(!kq())return $f(Error("User has not consented - not fetching biscotti id."));var a=L("PLAYER_VARS",{});if("1"==qb(a))return $f(Error("Biscotti ID is not available in private embed mode"));if(M("embeds_web_disable_ads_for_pfl")&&"EMBEDDED_PLAYER_MODE_PFL"===lq(a))return $f(Error("Biscotti id fetching has been disabled for pfl."));oq||(oq=bg(cq().then(pq),
function(b){return qq(2,b)}));
return oq}
function pq(a){a=a.xhr.responseText;if(0!=a.lastIndexOf(")]}'",0))throw new nq;a=JSON.parse(a.substr(4));if(1<(a.type||1))throw new nq;a=a.id;Zh(a);oq=hq(a);rq(18E5,2);return a}
function qq(a,b){b=new mq(b);Zh("");oq=iq(b);0<a&&rq(12E4,a-1);throw b;}
function rq(a,b){oi(function(){bg(cq().then(pq,function(c){return qq(b,c)}),bb)},a)}
function sq(){try{var a=A("yt.ads.biscotti.getId_");return a?a():Yh()}catch(b){return $f(b)}}
;function tq(a){if("1"!=qb(L("PLAYER_VARS",{}))){a&&Xh();try{sq().then(function(){},function(){}),oi(tq,18E5)}catch(b){Vh(b)}}}
;function uq(){this.xc=!0}
function vq(a){var b={},c=ne([]);c&&(b.Authorization=c,c=a=null==a?void 0:a.sessionIndex,void 0===c&&(c=Number(L("SESSION_INDEX",0)),c=isNaN(c)?0:c),M("voice_search_auth_header_removal")||(b["X-Goog-AuthUser"]=c),"INNERTUBE_HOST_OVERRIDE"in Lh||(b["X-Origin"]=window.location.origin),void 0===a&&"DELEGATED_SESSION_ID"in Lh&&(b["X-Goog-PageId"]=L("DELEGATED_SESSION_ID")));return b}
;var wq=Symbol("injectionDeps");function xq(a){this.name=a}
xq.prototype.toString=function(){return"InjectionToken("+this.name+")"};
function yq(){this.key=zq}
function Aq(){this.providers=new Map;this.i=new Map}
Aq.prototype.resolve=function(a){return a instanceof yq?Bq(this,a.key,[],!0):Bq(this,a,[])};
function Bq(a,b,c,d){d=void 0===d?!1:d;if(-1<c.indexOf(b))throw Error("Deps cycle for: "+b);if(a.i.has(b))return a.i.get(b);if(!a.providers.has(b)){if(d)return;throw Error("No provider for: "+b);}d=a.providers.get(b);c.push(b);if(d.Bc)var e=d.Bc;else if(d.Ac)e=d[wq]?Cq(a,d[wq],c):[],e=d.Ac.apply(d,fa(e));else if(d.Ib){e=d.Ib;var f=e[wq]?Cq(a,e[wq],c):[];e=new (Function.prototype.bind.apply(e,[null].concat(fa(f))))}else throw Error("Could not resolve providers for: "+b);c.pop();d.xp||a.i.set(b,e);
return e}
function Cq(a,b,c){return b?b.map(function(d){return d instanceof yq?Bq(a,d.key,c,!0):Bq(a,d,c)}):[]}
;var Dq;var Eq={identityType:"UNAUTHENTICATED_IDENTITY_TYPE_UNKNOWN"};var Fq=new Map([["dark","USER_INTERFACE_THEME_DARK"],["light","USER_INTERFACE_THEME_LIGHT"]]);function Gq(){var a=void 0===a?window.location.href:a;if(M("kevlar_disable_theme_param"))return null;ac(bc(5,a));try{var b=gi(a).theme;return Fq.get(b)||null}catch(c){}return null}
;function Hq(){this.i={};if(this.j=bj()){var a=aj("CONSISTENCY");a&&Iq(this,{encryptedTokenJarContents:a})}}
Hq.prototype.handleResponse=function(a,b){var c,d;b=(null==(c=b.Z.context)?void 0:null==(d=c.request)?void 0:d.consistencyTokenJars)||[];var e;if(a=null==(e=a.responseContext)?void 0:e.consistencyTokenJar){e=q(b);for(c=e.next();!c.done;c=e.next())delete this.i[c.value.encryptedTokenJarContents];Iq(this,a)}};
function Iq(a,b){if(b.encryptedTokenJarContents&&(a.i[b.encryptedTokenJarContents]=b,"string"===typeof b.expirationSeconds)){var c=Number(b.expirationSeconds);setTimeout(function(){delete a.i[b.encryptedTokenJarContents]},1E3*c);
a.j&&$i("CONSISTENCY",b.encryptedTokenJarContents,c,void 0,!0)}}
;var Jq=window.location.hostname.split(".").slice(-2).join(".");function Kq(){var a=L("LOCATION_PLAYABILITY_TOKEN");"TVHTML5"===L("INNERTUBE_CLIENT_NAME")&&(this.i=Lq(this))&&(a=this.i.get("yt-location-playability-token"));a&&(this.locationPlayabilityToken=a,this.j=void 0)}
var Mq;Kq.getInstance=function(){Mq=A("yt.clientLocationService.instance");Mq||(Mq=new Kq,z("yt.clientLocationService.instance",Mq));return Mq};
Kq.prototype.setLocationOnInnerTubeContext=function(a){a.client||(a.client={});this.j?(a.client.locationInfo||(a.client.locationInfo={}),a.client.locationInfo.latitudeE7=Math.floor(1E7*this.j.coords.latitude),a.client.locationInfo.longitudeE7=Math.floor(1E7*this.j.coords.longitude),a.client.locationInfo.horizontalAccuracyMeters=Math.round(this.j.coords.accuracy),a.client.locationInfo.forceLocationPlayabilityTokenRefresh=!0):this.locationPlayabilityToken&&(a.client.locationPlayabilityToken=this.locationPlayabilityToken)};
Kq.prototype.handleResponse=function(a){var b;a=null==(b=a.responseContext)?void 0:b.locationPlayabilityToken;void 0!==a&&(this.locationPlayabilityToken=a,this.j=void 0,"TVHTML5"===L("INNERTUBE_CLIENT_NAME")?(this.i=Lq(this))&&this.i.set("yt-location-playability-token",a,15552E3):$i("YT_CL",JSON.stringify({loctok:a}),15552E3,Jq,!0))};
function Lq(a){return void 0===a.i?new zj("yt-client-location"):a.i}
Kq.prototype.getCurrentPositionFromGeolocation=function(){var a=this;if(!(navigator&&navigator.geolocation&&navigator.geolocation.getCurrentPosition)||!M("web_enable_browser_geolocation_api")&&!M("enable_handoff_location_2fa_on_mweb"))return Promise.reject(Error("Geolocation unsupported"));var b=!1,c=1E4;M("enable_handoff_location_2fa_on_mweb")&&(b=!0,c=15E3);return new Promise(function(d,e){navigator.geolocation.getCurrentPosition(function(f){a.j=f;d(f)},function(f){e(f)},{enableHighAccuracy:b,
maximumAge:0,timeout:c})})};
Kq.prototype.createUnpluggedLocationInfo=function(a){var b={};a=a.coords;if(null==a?0:a.latitude)b.latitudeE7=Math.floor(1E7*a.latitude);if(null==a?0:a.longitude)b.longitudeE7=Math.floor(1E7*a.longitude);if(null==a?0:a.accuracy)b.locationRadiusMeters=Math.round(a.accuracy);return b};function Nq(a,b){var c;if((null==(c=a.signalServiceEndpoint)?0:c.signal)&&b.xa&&(c=b.xa[a.signalServiceEndpoint.signal]))return c();var d;if((null==(d=a.continuationCommand)?0:d.request)&&b.Pb&&(d=b.Pb[a.continuationCommand.request]))return d();for(var e in a)if(b.mb[e]&&(a=b.mb[e]))return a()}
;function Oq(a){return function(){return new a}}
;var Pq={},Qq=(Pq.WEB_UNPLUGGED="^unplugged/",Pq.WEB_UNPLUGGED_ONBOARDING="^unplugged/",Pq.WEB_UNPLUGGED_OPS="^unplugged/",Pq.WEB_UNPLUGGED_PUBLIC="^unplugged/",Pq.WEB_CREATOR="^creator/",Pq.WEB_KIDS="^kids/",Pq.WEB_EXPERIMENTS="^experiments/",Pq.WEB_MUSIC="^music/",Pq.WEB_REMIX="^music/",Pq.WEB_MUSIC_EMBEDDED_PLAYER="^music/",Pq.WEB_MUSIC_EMBEDDED_PLAYER="^main_app/|^sfv/",Pq);
function Rq(a){var b=void 0===b?"UNKNOWN_INTERFACE":b;if(1===a.length)return a[0];var c=Qq[b];if(c){var d=new RegExp(c),e=q(a);for(c=e.next();!c.done;c=e.next())if(c=c.value,d.exec(c))return c}var f=[];Object.entries(Qq).forEach(function(g){var h=q(g);g=h.next().value;h=h.next().value;b!==g&&f.push(h)});
d=new RegExp(f.join("|"));a.sort(function(g,h){return g.length-h.length});
e=q(a);for(c=e.next();!c.done;c=e.next())if(c=c.value,!d.exec(c))return c;return a[0]}
;function Sq(){}
Sq.prototype.s=function(a,b,c){b=void 0===b?{}:b;c=void 0===c?Eq:c;var d=a.clickTrackingParams,e=this.m,f=!1;f=void 0===f?!1:f;e=void 0===e?!1:e;var g=L("INNERTUBE_CONTEXT");if(g){g=vb(g);M("web_no_tracking_params_in_shell_killswitch")||delete g.clickTracking;g.client||(g.client={});var h=g.client;"MWEB"===h.clientName&&(h.clientFormFactor=L("IS_TABLET")?"LARGE_FORM_FACTOR":"SMALL_FORM_FACTOR");h.screenWidthPoints=window.innerWidth;h.screenHeightPoints=window.innerHeight;h.screenPixelDensity=Math.round(window.devicePixelRatio||
1);h.screenDensityFloat=window.devicePixelRatio||1;h.utcOffsetMinutes=-Math.floor((new Date).getTimezoneOffset());var k=void 0===k?!1:k;dj.getInstance();var m="USER_INTERFACE_THEME_LIGHT";gj(165)?m="USER_INTERFACE_THEME_DARK":gj(174)?m="USER_INTERFACE_THEME_LIGHT":!M("kevlar_legacy_browsers")&&window.matchMedia&&window.matchMedia("(prefers-color-scheme)").matches&&window.matchMedia("(prefers-color-scheme: dark)").matches&&(m="USER_INTERFACE_THEME_DARK");k=k?m:Gq()||m;h.userInterfaceTheme=k;if(!f){if(k=
nj())h.connectionType=k;M("web_log_effective_connection_type")&&(k=oj())&&(g.client.effectiveConnectionType=k)}var p;if(M("web_log_memory_total_kbytes")&&(null==(p=y.navigator)?0:p.deviceMemory)){var u;p=null==(u=y.navigator)?void 0:u.deviceMemory;g.client.memoryTotalKbytes=""+1E6*p}u=gi(y.location.href);!M("web_populate_internal_geo_killswitch")&&u.internalcountrycode&&(h.internalGeo=u.internalcountrycode);"MWEB"===h.clientName||"WEB"===h.clientName?(h.mainAppWebInfo={graftUrl:y.location.href},M("kevlar_woffle")&&
Yi.i&&(h.mainAppWebInfo.pwaInstallabilityStatus=Yi.i.i?"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED":"PWA_INSTALLABILITY_STATUS_UNKNOWN"),h.mainAppWebInfo.webDisplayMode=Zi(),h.mainAppWebInfo.isWebNativeShareAvailable=navigator&&void 0!==navigator.share):"TVHTML5"===h.clientName&&(!M("web_lr_app_quality_killswitch")&&(u=L("LIVING_ROOM_APP_QUALITY"))&&(h.tvAppInfo=Object.assign(h.tvAppInfo||{},{appQuality:u})),u=L("LIVING_ROOM_CERTIFICATION_SCOPE"))&&(h.tvAppInfo=Object.assign(h.tvAppInfo||{},{certificationScope:u}));
if(!M("web_populate_time_zone_itc_killswitch")){b:{if("undefined"!==typeof Intl)try{var x=(new Intl.DateTimeFormat).resolvedOptions().timeZone;break b}catch(Na){}x=void 0}x&&(h.timeZone=x)}(x=Qh())?h.experimentsToken=x:delete h.experimentsToken;x=Rh();Hq.i||(Hq.i=new Hq);h=Hq.i.i;u=[];p=0;for(var v in h)u[p++]=h[v];g.request=Object.assign({},g.request,{internalExperimentFlags:x,consistencyTokenJars:u});!M("web_prequest_context_killswitch")&&(v=L("INNERTUBE_CONTEXT_PREQUEST_CONTEXT"))&&(g.request.externalPrequestContext=
v);x=dj.getInstance();v=gj(58);x=x.get("gsml","");g.user=Object.assign({},g.user);v&&(g.user.enableSafetyMode=v);x&&(g.user.lockedSafetyMode=!0);M("warm_op_csn_cleanup")?e&&(f=Wp())&&(g.clientScreenNonce=f):!f&&(f=Wp())&&(g.clientScreenNonce=f);d&&(g.clickTracking={clickTrackingParams:d});if(d=A("yt.mdx.remote.remoteClient_"))g.remoteClient=d;M("web_enable_client_location_service")&&Kq.getInstance().setLocationOnInnerTubeContext(g);try{var E=ji(),G=E.bid;delete E.bid;g.adSignalsInfo={params:[],bid:G};
var H=q(Object.entries(E));for(var R=H.next();!R.done;R=H.next()){var N=q(R.value),S=N.next().value,ja=N.next().value;E=S;G=ja;d=void 0;null==(d=g.adSignalsInfo.params)||d.push({key:E,value:""+G})}}catch(Na){Dp(Na)}H=g}else Dp(Error("Error: No InnerTubeContext shell provided in ytconfig.")),H={};H={context:H};if(R=this.i(a)){this.j(H,R,b);var O;b="/youtubei/v1/"+Rq(this.l());var Ba;(a=null==(O=a.commandMetadata)?void 0:null==(Ba=O.webCommandMetadata)?void 0:Ba.apiUrl)&&(b=a);O=b;(Ba=L("INNERTUBE_HOST_OVERRIDE"))&&
(O=String(Ba)+String(dc(O)));Ba={};Ba.key=L("INNERTUBE_API_KEY");M("json_condensed_response")&&(Ba.prettyPrint="false");O=hi(O,Ba||{},!1);O={input:O,oa:jq(O),Z:H,config:Object.assign({},void 0)};O.config.Ea?O.config.Ea.identity=c:O.config.Ea={identity:c};return O}Dp(new Q("Error: Failed to create Request from Command.",a))};
da.Object.defineProperties(Sq.prototype,{m:{configurable:!0,enumerable:!0,get:function(){return!1}}});function Tq(){}
r(Tq,Sq);Tq.prototype.s=function(){return{input:"/getDatasyncIdsEndpoint",oa:jq("/getDatasyncIdsEndpoint","GET"),Z:{}}};
Tq.prototype.l=function(){return[]};
Tq.prototype.i=function(){};
Tq.prototype.j=function(){};var Uq={},Vq=(Uq.GET_DATASYNC_IDS=Oq(Tq),Uq);function Wq(a){var b=Ja.apply(1,arguments);if(!Xq(a)||b.some(function(d){return!Xq(d)}))throw Error("Only objects may be merged.");
b=q(b);for(var c=b.next();!c.done;c=b.next())Yq(a,c.value);return a}
function Yq(a,b){for(var c in b)if(Xq(b[c])){if(c in a&&!Xq(a[c]))throw Error("Cannot merge an object into a non-object.");c in a||(a[c]={});Yq(a[c],b[c])}else if(Zq(b[c])){if(c in a&&!Zq(a[c]))throw Error("Cannot merge an array into a non-array.");c in a||(a[c]=[]);$q(a[c],b[c])}else a[c]=b[c];return a}
function $q(a,b){b=q(b);for(var c=b.next();!c.done;c=b.next())c=c.value,Xq(c)?a.push(Yq({},c)):Zq(c)?a.push($q([],c)):a.push(c);return a}
function Xq(a){return"object"===typeof a&&!Array.isArray(a)}
function Zq(a){return"object"===typeof a&&Array.isArray(a)}
;function ar(a,b){Gl.call(this,1,arguments);this.timer=b}
r(ar,Gl);var br=new Hl("aft-recorded",ar);var cr=window;function dr(){this.timing={};this.clearResourceTimings=function(){};
this.webkitClearResourceTimings=function(){};
this.mozClearResourceTimings=function(){};
this.msClearResourceTimings=function(){};
this.oClearResourceTimings=function(){}}
var T=cr.performance||cr.mozPerformance||cr.msPerformance||cr.webkitPerformance||new dr;var er=!1,fr={'script[name="scheduler/scheduler"]':"sj",'script[name="player/base"]':"pj",'link[rel="stylesheet"][name="www-player"]':"pc",'link[rel="stylesheet"][name="player/www-player"]':"pc",'script[name="desktop_polymer/desktop_polymer"]':"dpj",'link[rel="import"][name="desktop_polymer"]':"dph",'script[name="mobile-c3"]':"mcj",'link[rel="stylesheet"][name="mobile-c3"]':"mcc",'script[name="player-plasma-ias-phone/base"]':"mcppj",'script[name="player-plasma-ias-tablet/base"]':"mcptj",'link[rel="stylesheet"][name="mobile-polymer-player-ias"]':"mcpc",
'link[rel="stylesheet"][name="mobile-polymer-player-svg-ias"]':"mcpsc",'script[name="mobile_blazer_core_mod"]':"mbcj",'link[rel="stylesheet"][name="mobile_blazer_css"]':"mbc",'script[name="mobile_blazer_logged_in_users_mod"]':"mbliuj",'script[name="mobile_blazer_logged_out_users_mod"]':"mblouj",'script[name="mobile_blazer_noncore_mod"]':"mbnj","#player_css":"mbpc",'script[name="mobile_blazer_desktopplayer_mod"]':"mbpj",'link[rel="stylesheet"][name="mobile_blazer_tablet_css"]':"mbtc",'script[name="mobile_blazer_watch_mod"]':"mbwj"};
Wa(T.clearResourceTimings||T.webkitClearResourceTimings||T.mozClearResourceTimings||T.msClearResourceTimings||T.oClearResourceTimings||bb,T);function gr(a){var b=hr(a);if(b.aft)return b.aft;a=L((a||"")+"TIMING_AFT_KEYS",["ol"]);for(var c=a.length,d=0;d<c;d++){var e=b[a[d]];if(e)return e}return NaN}
function ir(){var a;if(M("csi_use_performance_navigation_timing")){var b,c,d,e=null==T?void 0:null==(a=T.getEntriesByType)?void 0:null==(b=a.call(T,"navigation"))?void 0:null==(c=b[0])?void 0:null==(d=c.toJSON)?void 0:d.call(c);e?(e.requestStart=jr(e.requestStart),e.responseEnd=jr(e.responseEnd),e.redirectStart=jr(e.redirectStart),e.redirectEnd=jr(e.redirectEnd),e.domainLookupEnd=jr(e.domainLookupEnd),e.connectStart=jr(e.connectStart),e.connectEnd=jr(e.connectEnd),e.responseStart=jr(e.responseStart),
e.secureConnectionStart=jr(e.secureConnectionStart),e.domainLookupStart=jr(e.domainLookupStart),e.isPerformanceNavigationTiming=!0,a=e):a=T.timing}else a=T.timing;return a}
function kr(){return M("csi_use_time_origin")&&T.timeOrigin?Math.floor(T.timeOrigin):T.timing.navigationStart}
function jr(a){return Math.round(kr()+a)}
function lr(a){var b;(b=A("ytcsi."+(a||"")+"data_"))||(b={tick:{},info:{}},z("ytcsi."+(a||"")+"data_",b));return b}
function mr(a){a=lr(a);a.info||(a.info={});return a.info}
function hr(a){a=lr(a);a.tick||(a.tick={});return a.tick}
function nr(a){a=lr(a);if(a.gel){var b=a.gel;b.gelInfos||(b.gelInfos={});b.gelTicks||(b.gelTicks={})}else a.gel={gelTicks:{},gelInfos:{}};return a.gel}
function or(a){a=nr(a);a.gelInfos||(a.gelInfos={});return a.gelInfos}
function pr(a){var b=lr(a).nonce;b||(b=Lp(),lr(a).nonce=b);return b}
function qr(a){var b=hr(a||""),c=gr(a);c&&!er&&(Ml(br,new ar(Math.round(c-b._start),a)),er=!0)}
function rr(a,b){for(var c=q(Object.keys(b)),d=c.next();!d.done;d=c.next())if(d=d.value,!Object.keys(a).includes(d)||"object"===typeof b[d]&&!rr(a[d],b[d]))return!1;return!0}
;function sr(){if(T.getEntriesByType){var a=T.getEntriesByType("paint");if(a=ib(a,function(b){return"first-paint"===b.name}))return jr(a.startTime)}a=T.timing;
return a.ac?Math.max(0,a.ac):0}
;function tr(){var a=A("ytcsi.debug");a||(a=[],z("ytcsi.debug",a),z("ytcsi.reference",{}));return a}
function ur(a){a=a||"";var b=A("ytcsi.reference");b||(tr(),b=A("ytcsi.reference"));if(b[a])return b[a];var c=tr(),d={timerName:a,info:{},tick:{},span:{},jspbInfo:[]};c.push(d);return b[a]=d}
;var U={},vr=(U.auto_search="LATENCY_ACTION_AUTO_SEARCH",U.ad_to_ad="LATENCY_ACTION_AD_TO_AD",U.ad_to_video="LATENCY_ACTION_AD_TO_VIDEO",U["analytics.explore"]="LATENCY_ACTION_CREATOR_ANALYTICS_EXPLORE",U.app_startup="LATENCY_ACTION_APP_STARTUP",U["artist.analytics"]="LATENCY_ACTION_CREATOR_ARTIST_ANALYTICS",U["artist.events"]="LATENCY_ACTION_CREATOR_ARTIST_CONCERTS",U["artist.presskit"]="LATENCY_ACTION_CREATOR_ARTIST_PROFILE",U.browse="LATENCY_ACTION_BROWSE",U.cast_splash="LATENCY_ACTION_CAST_SPLASH",
U.channels="LATENCY_ACTION_CHANNELS",U.creator_channel_dashboard="LATENCY_ACTION_CREATOR_CHANNEL_DASHBOARD",U["channel.analytics"]="LATENCY_ACTION_CREATOR_CHANNEL_ANALYTICS",U["channel.comments"]="LATENCY_ACTION_CREATOR_CHANNEL_COMMENTS",U["channel.content"]="LATENCY_ACTION_CREATOR_POST_LIST",U["channel.copyright"]="LATENCY_ACTION_CREATOR_CHANNEL_COPYRIGHT",U["channel.editing"]="LATENCY_ACTION_CREATOR_CHANNEL_EDITING",U["channel.monetization"]="LATENCY_ACTION_CREATOR_CHANNEL_MONETIZATION",U["channel.music"]=
"LATENCY_ACTION_CREATOR_CHANNEL_MUSIC",U["channel.playlists"]="LATENCY_ACTION_CREATOR_CHANNEL_PLAYLISTS",U["channel.translations"]="LATENCY_ACTION_CREATOR_CHANNEL_TRANSLATIONS",U["channel.videos"]="LATENCY_ACTION_CREATOR_CHANNEL_VIDEOS",U["channel.live_streaming"]="LATENCY_ACTION_CREATOR_LIVE_STREAMING",U.chips="LATENCY_ACTION_CHIPS",U["dialog.copyright_strikes"]="LATENCY_ACTION_CREATOR_DIALOG_COPYRIGHT_STRIKES",U["dialog.uploads"]="LATENCY_ACTION_CREATOR_DIALOG_UPLOADS",U.direct_playback="LATENCY_ACTION_DIRECT_PLAYBACK",
U.embed="LATENCY_ACTION_EMBED",U.entity_key_serialization_perf="LATENCY_ACTION_ENTITY_KEY_SERIALIZATION_PERF",U.entity_key_deserialization_perf="LATENCY_ACTION_ENTITY_KEY_DESERIALIZATION_PERF",U.explore="LATENCY_ACTION_EXPLORE",U.home="LATENCY_ACTION_HOME",U.library="LATENCY_ACTION_LIBRARY",U.live="LATENCY_ACTION_LIVE",U.live_pagination="LATENCY_ACTION_LIVE_PAGINATION",U.onboarding="LATENCY_ACTION_ONBOARDING",U.parent_profile_settings="LATENCY_ACTION_KIDS_PARENT_PROFILE_SETTINGS",U.parent_tools_collection=
"LATENCY_ACTION_PARENT_TOOLS_COLLECTION",U.parent_tools_dashboard="LATENCY_ACTION_PARENT_TOOLS_DASHBOARD",U.player_att="LATENCY_ACTION_PLAYER_ATTESTATION",U["post.comments"]="LATENCY_ACTION_CREATOR_POST_COMMENTS",U["post.edit"]="LATENCY_ACTION_CREATOR_POST_EDIT",U.prebuffer="LATENCY_ACTION_PREBUFFER",U.prefetch="LATENCY_ACTION_PREFETCH",U.profile_settings="LATENCY_ACTION_KIDS_PROFILE_SETTINGS",U.profile_switcher="LATENCY_ACTION_LOGIN",U.reel_watch="LATENCY_ACTION_REEL_WATCH",U.results="LATENCY_ACTION_RESULTS",
U.search_ui="LATENCY_ACTION_SEARCH_UI",U.search_suggest="LATENCY_ACTION_SUGGEST",U.search_zero_state="LATENCY_ACTION_SEARCH_ZERO_STATE",U.secret_code="LATENCY_ACTION_KIDS_SECRET_CODE",U.seek="LATENCY_ACTION_PLAYER_SEEK",U.settings="LATENCY_ACTION_SETTINGS",U.tenx="LATENCY_ACTION_TENX",U.video_to_ad="LATENCY_ACTION_VIDEO_TO_AD",U.watch="LATENCY_ACTION_WATCH",U.watch_it_again="LATENCY_ACTION_KIDS_WATCH_IT_AGAIN",U["watch,watch7"]="LATENCY_ACTION_WATCH",U["watch,watch7_html5"]="LATENCY_ACTION_WATCH",
U["watch,watch7ad"]="LATENCY_ACTION_WATCH",U["watch,watch7ad_html5"]="LATENCY_ACTION_WATCH",U.wn_comments="LATENCY_ACTION_LOAD_COMMENTS",U.ww_rqs="LATENCY_ACTION_WHO_IS_WATCHING",U["video.analytics"]="LATENCY_ACTION_CREATOR_VIDEO_ANALYTICS",U["video.comments"]="LATENCY_ACTION_CREATOR_VIDEO_COMMENTS",U["video.edit"]="LATENCY_ACTION_CREATOR_VIDEO_EDIT",U["video.editor"]="LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR",U["video.editor_async"]="LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR_ASYNC",U["video.live_settings"]=
"LATENCY_ACTION_CREATOR_VIDEO_LIVE_SETTINGS",U["video.live_streaming"]="LATENCY_ACTION_CREATOR_VIDEO_LIVE_STREAMING",U["video.monetization"]="LATENCY_ACTION_CREATOR_VIDEO_MONETIZATION",U["video.translations"]="LATENCY_ACTION_CREATOR_VIDEO_TRANSLATIONS",U.voice_assistant="LATENCY_ACTION_VOICE_ASSISTANT",U.cast_load_by_entity_to_watch="LATENCY_ACTION_CAST_LOAD_BY_ENTITY_TO_WATCH",U.networkless_performance="LATENCY_ACTION_NETWORKLESS_PERFORMANCE",U),V={},wr=(V.ad_allowed="adTypesAllowed",V.yt_abt="adBreakType",
V.ad_cpn="adClientPlaybackNonce",V.ad_docid="adVideoId",V.yt_ad_an="adNetworks",V.ad_at="adType",V.aida="appInstallDataAgeMs",V.browse_id="browseId",V.p="httpProtocol",V.t="transportProtocol",V.cs="commandSource",V.cpn="clientPlaybackNonce",V.ccs="creatorInfo.creatorCanaryState",V.ctop="creatorInfo.topEntityType",V.csn="clientScreenNonce",V.docid="videoId",V.GetHome_rid="requestIds",V.GetSearch_rid="requestIds",V.GetPlayer_rid="requestIds",V.GetWatchNext_rid="requestIds",V.GetBrowse_rid="requestIds",
V.GetLibrary_rid="requestIds",V.is_continuation="isContinuation",V.is_nav="isNavigation",V.b_p="kabukiInfo.browseParams",V.is_prefetch="kabukiInfo.isPrefetch",V.is_secondary_nav="kabukiInfo.isSecondaryNav",V.nav_type="kabukiInfo.navigationType",V.prev_browse_id="kabukiInfo.prevBrowseId",V.query_source="kabukiInfo.querySource",V.voz_type="kabukiInfo.vozType",V.yt_lt="loadType",V.mver="creatorInfo.measurementVersion",V.yt_ad="isMonetized",V.nr="webInfo.navigationReason",V.nrsu="navigationRequestedSameUrl",
V.pnt="performanceNavigationTiming",V.prt="playbackRequiresTap",V.plt="playerInfo.playbackType",V.pis="playerInfo.playerInitializedState",V.paused="playerInfo.isPausedOnLoad",V.yt_pt="playerType",V.fmt="playerInfo.itag",V.yt_pl="watchInfo.isPlaylist",V.yt_pre="playerInfo.preloadType",V.yt_ad_pr="prerollAllowed",V.pa="previousAction",V.yt_red="isRedSubscriber",V.rce="mwebInfo.responseContentEncoding",V.rc="resourceInfo.resourceCache",V.scrh="screenHeight",V.scrw="screenWidth",V.st="serverTimeMs",V.ssdm=
"shellStartupDurationMs",V.br_trs="tvInfo.bedrockTriggerState",V.kebqat="kabukiInfo.earlyBrowseRequestInfo.abandonmentType",V.kebqa="kabukiInfo.earlyBrowseRequestInfo.adopted",V.label="tvInfo.label",V.is_mdx="tvInfo.isMdx",V.preloaded="tvInfo.isPreloaded",V.aac_type="tvInfo.authAccessCredentialType",V.upg_player_vis="playerInfo.visibilityState",V.query="unpluggedInfo.query",V.upg_chip_ids_string="unpluggedInfo.upgChipIdsString",V.yt_vst="videoStreamType",V.vph="viewportHeight",V.vpw="viewportWidth",
V.yt_vis="isVisible",V.rcl="mwebInfo.responseContentLength",V.GetSettings_rid="requestIds",V.GetTrending_rid="requestIds",V.GetMusicSearchSuggestions_rid="requestIds",V.REQUEST_ID="requestIds",V),xr="isContinuation isNavigation kabukiInfo.earlyBrowseRequestInfo.adopted kabukiInfo.isPrefetch kabukiInfo.isSecondaryNav isMonetized navigationRequestedSameUrl performanceNavigationTiming playerInfo.isPausedOnLoad prerollAllowed isRedSubscriber tvInfo.isMdx tvInfo.isPreloaded isVisible watchInfo.isPlaylist playbackRequiresTap".split(" "),
yr={},zr=(yr.ccs="CANARY_STATE_",yr.mver="MEASUREMENT_VERSION_",yr.pis="PLAYER_INITIALIZED_STATE_",yr.yt_pt="LATENCY_PLAYER_",yr.pa="LATENCY_ACTION_",yr.ctop="TOP_ENTITY_TYPE_",yr.yt_vst="VIDEO_STREAM_TYPE_",yr),Ar="all_vc ap aq c cbr cbrand cbrver cmodel cos cosver cplatform ctheme cver ei l_an l_mm plid srt yt_fss yt_li vpst vpni2 vpil2 icrc icrt pa GetAccountOverview_rid GetHistory_rid cmt d_vpct d_vpnfi d_vpni nsru pc pfa pfeh pftr pnc prerender psc rc start tcrt tcrc ssr vpr vps yt_abt yt_fn yt_fs yt_pft yt_pre yt_pt yt_pvis ytu_pvis yt_ref yt_sts tds".split(" ");
function Br(a){return vr[a]||"LATENCY_ACTION_UNKNOWN"}
function Cr(a,b,c){c=nr(c);if(c.gelInfos)c.gelInfos[a]=!0;else{var d={};c.gelInfos=(d[a]=!0,d)}if(a.match("_rid")){var e=a.split("_rid")[0];a="REQUEST_ID"}if(a in wr){c=wr[a];0<=db(xr,c)&&(b=!!b);a in zr&&"string"===typeof b&&(b=zr[a]+b.toUpperCase());a=b;b=c.split(".");for(var f=d={},g=0;g<b.length-1;g++){var h=b[g];f[h]={};f=f[h]}f[b[b.length-1]]="requestIds"===c?[{id:a,endpoint:e}]:a;return Wq({},d)}0<=db(Ar,a)||Ep(new Q("Unknown label logged with GEL CSI",a))}
;var W={LATENCY_ACTION_KIDS_PROFILE_SWITCHER:90,LATENCY_ACTION_OFFLINE_THUMBNAIL_TRANSFER:100,LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR_ASYNC:46,LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR:37,LATENCY_ACTION_SPINNER_DISPLAYED:14,LATENCY_ACTION_PLAYABILITY_CHECK:10,LATENCY_ACTION_PROCESS:9,LATENCY_ACTION_APP_STARTUP:5,LATENCY_ACTION_LOG_PAYMENT_SERVER_ANALYTICS_RPC:173,LATENCY_ACTION_GET_PAYMENT_INSTRUMENTS_PARAMS_RPC:172,LATENCY_ACTION_GET_FIX_INSTRUMENT_PARAMS_RPC:171,LATENCY_ACTION_RESUME_SUBSCRIPTION_RPC:170,
LATENCY_ACTION_PAUSE_SUBSCRIPTION_RPC:169,LATENCY_ACTION_GET_OFFLINE_UPSELL_RPC:168,LATENCY_ACTION_GET_OFFERS_RPC:167,LATENCY_ACTION_GET_CANCELLATION_YT_FLOW_RPC:166,LATENCY_ACTION_GET_CANCELLATION_FLOW_RPC:165,LATENCY_ACTION_UPDATE_CROSS_DEVICE_OFFLINE_STATE_RPC:164,LATENCY_ACTION_GET_OFFER_DETAILS_RPC:163,LATENCY_ACTION_CANCEL_RECURRENCE_TRANSACTION_RPC:162,LATENCY_ACTION_GET_TIP_MODULE_RPC:161,LATENCY_ACTION_HANDLE_TRANSACTION_RPC:160,LATENCY_ACTION_COMPLETE_TRANSACTION_RPC:159,LATENCY_ACTION_GET_CART_RPC:158,
LATENCY_ACTION_THUMBNAIL_FETCH:156,LATENCY_ACTION_ABANDONED_DIRECT_PLAYBACK:154,LATENCY_ACTION_SHARE_VIDEO:153,LATENCY_ACTION_AD_TO_VIDEO_INT:152,LATENCY_ACTION_ABANDONED_BROWSE:151,LATENCY_ACTION_PLAYER_ROTATION:150,LATENCY_ACTION_SHOPPING_IN_APP:124,LATENCY_ACTION_PLAYER_ATTESTATION:121,LATENCY_ACTION_PLAYER_SEEK:119,LATENCY_ACTION_SUPER_STICKER_BUY_FLOW:114,LATENCY_ACTION_BLOCKS_PERFORMANCE:148,LATENCY_ACTION_ASSISTANT_QUERY:138,LATENCY_ACTION_ASSISTANT_SETTINGS:137,LATENCY_ACTION_ENTITY_KEY_DESERIALIZATION_PERF:129,
LATENCY_ACTION_ENTITY_KEY_SERIALIZATION_PERF:128,LATENCY_ACTION_PROOF_OF_ORIGIN_TOKEN_CREATE:127,LATENCY_ACTION_EMBEDS_SDK_INITIALIZATION:123,LATENCY_ACTION_NETWORKLESS_PERFORMANCE:122,LATENCY_ACTION_DOWNLOADS_EXPANSION:133,LATENCY_ACTION_ENTITY_TRANSFORM:131,LATENCY_ACTION_DOWNLOADS_COMPATIBILITY_LAYER:96,LATENCY_ACTION_EMBEDS_SET_VIDEO:95,LATENCY_ACTION_SETTINGS:93,LATENCY_ACTION_ABANDONED_STARTUP:81,LATENCY_ACTION_MEDIA_BROWSER_ALARM_PLAY:80,LATENCY_ACTION_MEDIA_BROWSER_SEARCH:79,LATENCY_ACTION_MEDIA_BROWSER_LOAD_TREE:78,
LATENCY_ACTION_WHO_IS_WATCHING:77,LATENCY_ACTION_CAST_LOAD_BY_ENTITY_TO_WATCH:76,LATENCY_ACTION_LITE_SWITCH_ACCOUNT:73,LATENCY_ACTION_ELEMENTS_PERFORMANCE:70,LATENCY_ACTION_LOCATION_SIGNAL_COLLECTION:69,LATENCY_ACTION_MODIFY_CHANNEL_NOTIFICATION:65,LATENCY_ACTION_OFFLINE_STORE_START:61,LATENCY_ACTION_REEL_EDITOR:58,LATENCY_ACTION_CHANNEL_SUBSCRIBE:56,LATENCY_ACTION_CHANNEL_PREVIEW:55,LATENCY_ACTION_PREFETCH:52,LATENCY_ACTION_ABANDONED_WATCH:45,LATENCY_ACTION_LOAD_COMMENT_REPLIES:26,LATENCY_ACTION_LOAD_COMMENTS:25,
LATENCY_ACTION_EDIT_COMMENT:24,LATENCY_ACTION_NEW_COMMENT:23,LATENCY_ACTION_OFFLINE_SHARING_RECEIVER_PAIRING:19,LATENCY_ACTION_EMBED:18,LATENCY_ACTION_MDX_LAUNCH:15,LATENCY_ACTION_RESOLVE_URL:13,LATENCY_ACTION_CAST_SPLASH:149,LATENCY_ACTION_MDX_CAST:120,LATENCY_ACTION_MDX_COMMAND:12,LATENCY_ACTION_REEL_SELECT_SEGMENT:136,LATENCY_ACTION_ACCELERATED_EFFECTS:145,LATENCY_ACTION_UPLOAD_AUDIO_MIXER:147,LATENCY_ACTION_SHORTS_CLIENT_SIDE_RENDERING:157,LATENCY_ACTION_SHORTS_SEG_IMP_TRANSCODING:146,LATENCY_ACTION_SHORTS_AUDIO_PICKER_PLAYBACK:130,
LATENCY_ACTION_SHORTS_WAVEFORM_DOWNLOAD:125,LATENCY_ACTION_SHORTS_VIDEO_INGESTION:155,LATENCY_ACTION_SHORTS_GALLERY:107,LATENCY_ACTION_SHORTS_TRIM:105,LATENCY_ACTION_SHORTS_EDIT:104,LATENCY_ACTION_SHORTS_CAMERA:103,LATENCY_ACTION_PARENT_TOOLS_DASHBOARD:102,LATENCY_ACTION_PARENT_TOOLS_COLLECTION:101,LATENCY_ACTION_MUSIC_LOAD_RECOMMENDED_MEDIA_ITEMS:116,LATENCY_ACTION_MUSIC_LOAD_MEDIA_ITEMS:115,LATENCY_ACTION_MUSIC_ALBUM_DETAIL:72,LATENCY_ACTION_MUSIC_PLAYLIST_DETAIL:71,LATENCY_ACTION_CHIPS:68,LATENCY_ACTION_SEARCH_ZERO_STATE:67,
LATENCY_ACTION_LIVE_PAGINATION:117,LATENCY_ACTION_LIVE:20,LATENCY_ACTION_PREBUFFER:40,LATENCY_ACTION_TENX:39,LATENCY_ACTION_KIDS_PROFILE_SETTINGS:94,LATENCY_ACTION_KIDS_WATCH_IT_AGAIN:92,LATENCY_ACTION_KIDS_SECRET_CODE:91,LATENCY_ACTION_KIDS_PARENT_PROFILE_SETTINGS:89,LATENCY_ACTION_KIDS_ONBOARDING:88,LATENCY_ACTION_KIDS_VOICE_SEARCH:82,LATENCY_ACTION_KIDS_CURATED_COLLECTION:62,LATENCY_ACTION_KIDS_LIBRARY:53,LATENCY_ACTION_CREATOR_VIDEO_TRANSLATIONS:38,LATENCY_ACTION_CREATOR_VIDEO_MONETIZATION:74,
LATENCY_ACTION_CREATOR_VIDEO_LIVE_STREAMING:141,LATENCY_ACTION_CREATOR_VIDEO_LIVE_SETTINGS:142,LATENCY_ACTION_CREATOR_VIDEO_EDITOR_ASYNC:51,LATENCY_ACTION_CREATOR_VIDEO_EDITOR:50,LATENCY_ACTION_CREATOR_VIDEO_EDIT:36,LATENCY_ACTION_CREATOR_VIDEO_COMMENTS:34,LATENCY_ACTION_CREATOR_VIDEO_ANALYTICS:33,LATENCY_ACTION_CREATOR_POST_LIST:112,LATENCY_ACTION_CREATOR_POST_EDIT:110,LATENCY_ACTION_CREATOR_POST_COMMENTS:111,LATENCY_ACTION_CREATOR_LIVE_STREAMING:108,LATENCY_ACTION_CREATOR_DIALOG_VIDEO_COPYRIGHT:174,
LATENCY_ACTION_CREATOR_DIALOG_UPLOADS:86,LATENCY_ACTION_CREATOR_DIALOG_COPYRIGHT_STRIKES:87,LATENCY_ACTION_CREATOR_CHANNEL_VIDEOS:32,LATENCY_ACTION_CREATOR_CHANNEL_TRANSLATIONS:48,LATENCY_ACTION_CREATOR_CHANNEL_PLAYLISTS:139,LATENCY_ACTION_CREATOR_CHANNEL_MUSIC:99,LATENCY_ACTION_CREATOR_CHANNEL_MONETIZATION:43,LATENCY_ACTION_CREATOR_CHANNEL_EDITING:113,LATENCY_ACTION_CREATOR_CHANNEL_DASHBOARD:49,LATENCY_ACTION_CREATOR_CHANNEL_COPYRIGHT:44,LATENCY_ACTION_CREATOR_CHANNEL_COMMENTS:66,LATENCY_ACTION_CREATOR_CHANNEL_ANALYTICS:31,
LATENCY_ACTION_CREATOR_ARTIST_PROFILE:85,LATENCY_ACTION_CREATOR_ARTIST_CONCERTS:84,LATENCY_ACTION_CREATOR_ARTIST_ANALYTICS:83,LATENCY_ACTION_CREATOR_ANALYTICS_EXPLORE:140,LATENCY_ACTION_STORYBOARD_THUMBNAILS:118,LATENCY_ACTION_SEARCH_THUMBNAILS:59,LATENCY_ACTION_ON_DEVICE_MODEL_DOWNLOAD:54,LATENCY_ACTION_VOICE_ASSISTANT:47,LATENCY_ACTION_SEARCH_UI:35,LATENCY_ACTION_SUGGEST:30,LATENCY_ACTION_AUTO_SEARCH:126,LATENCY_ACTION_DOWNLOADS:98,LATENCY_ACTION_EXPLORE:75,LATENCY_ACTION_VIDEO_LIST:63,LATENCY_ACTION_HOME_RESUME:60,
LATENCY_ACTION_SUBSCRIPTIONS_LIST:57,LATENCY_ACTION_THUMBNAIL_LOAD:42,LATENCY_ACTION_FIRST_THUMBNAIL_LOAD:29,LATENCY_ACTION_SUBSCRIPTIONS_FEED:109,LATENCY_ACTION_SUBSCRIPTIONS:28,LATENCY_ACTION_TRENDING:27,LATENCY_ACTION_LIBRARY:21,LATENCY_ACTION_VIDEO_THUMBNAIL:8,LATENCY_ACTION_SHOW_MORE:7,LATENCY_ACTION_VIDEO_PREVIEW:6,LATENCY_ACTION_PREBUFFER_VIDEO:144,LATENCY_ACTION_PREFETCH_VIDEO:143,LATENCY_ACTION_DIRECT_PLAYBACK:132,LATENCY_ACTION_REEL_WATCH:41,LATENCY_ACTION_AD_TO_AD:22,LATENCY_ACTION_VIDEO_TO_AD:17,
LATENCY_ACTION_AD_TO_VIDEO:16,LATENCY_ACTION_ONBOARDING:135,LATENCY_ACTION_LOGIN:97,LATENCY_ACTION_BROWSE:11,LATENCY_ACTION_CHANNELS:4,LATENCY_ACTION_WATCH:3,LATENCY_ACTION_RESULTS:2,LATENCY_ACTION_HOME:1,LATENCY_ACTION_STARTUP:106,LATENCY_ACTION_UNKNOWN:0};W[W.LATENCY_ACTION_KIDS_PROFILE_SWITCHER]="LATENCY_ACTION_KIDS_PROFILE_SWITCHER";W[W.LATENCY_ACTION_OFFLINE_THUMBNAIL_TRANSFER]="LATENCY_ACTION_OFFLINE_THUMBNAIL_TRANSFER";W[W.LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR_ASYNC]="LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR_ASYNC";
W[W.LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR]="LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR";W[W.LATENCY_ACTION_SPINNER_DISPLAYED]="LATENCY_ACTION_SPINNER_DISPLAYED";W[W.LATENCY_ACTION_PLAYABILITY_CHECK]="LATENCY_ACTION_PLAYABILITY_CHECK";W[W.LATENCY_ACTION_PROCESS]="LATENCY_ACTION_PROCESS";W[W.LATENCY_ACTION_APP_STARTUP]="LATENCY_ACTION_APP_STARTUP";W[W.LATENCY_ACTION_LOG_PAYMENT_SERVER_ANALYTICS_RPC]="LATENCY_ACTION_LOG_PAYMENT_SERVER_ANALYTICS_RPC";
W[W.LATENCY_ACTION_GET_PAYMENT_INSTRUMENTS_PARAMS_RPC]="LATENCY_ACTION_GET_PAYMENT_INSTRUMENTS_PARAMS_RPC";W[W.LATENCY_ACTION_GET_FIX_INSTRUMENT_PARAMS_RPC]="LATENCY_ACTION_GET_FIX_INSTRUMENT_PARAMS_RPC";W[W.LATENCY_ACTION_RESUME_SUBSCRIPTION_RPC]="LATENCY_ACTION_RESUME_SUBSCRIPTION_RPC";W[W.LATENCY_ACTION_PAUSE_SUBSCRIPTION_RPC]="LATENCY_ACTION_PAUSE_SUBSCRIPTION_RPC";W[W.LATENCY_ACTION_GET_OFFLINE_UPSELL_RPC]="LATENCY_ACTION_GET_OFFLINE_UPSELL_RPC";W[W.LATENCY_ACTION_GET_OFFERS_RPC]="LATENCY_ACTION_GET_OFFERS_RPC";
W[W.LATENCY_ACTION_GET_CANCELLATION_YT_FLOW_RPC]="LATENCY_ACTION_GET_CANCELLATION_YT_FLOW_RPC";W[W.LATENCY_ACTION_GET_CANCELLATION_FLOW_RPC]="LATENCY_ACTION_GET_CANCELLATION_FLOW_RPC";W[W.LATENCY_ACTION_UPDATE_CROSS_DEVICE_OFFLINE_STATE_RPC]="LATENCY_ACTION_UPDATE_CROSS_DEVICE_OFFLINE_STATE_RPC";W[W.LATENCY_ACTION_GET_OFFER_DETAILS_RPC]="LATENCY_ACTION_GET_OFFER_DETAILS_RPC";W[W.LATENCY_ACTION_CANCEL_RECURRENCE_TRANSACTION_RPC]="LATENCY_ACTION_CANCEL_RECURRENCE_TRANSACTION_RPC";
W[W.LATENCY_ACTION_GET_TIP_MODULE_RPC]="LATENCY_ACTION_GET_TIP_MODULE_RPC";W[W.LATENCY_ACTION_HANDLE_TRANSACTION_RPC]="LATENCY_ACTION_HANDLE_TRANSACTION_RPC";W[W.LATENCY_ACTION_COMPLETE_TRANSACTION_RPC]="LATENCY_ACTION_COMPLETE_TRANSACTION_RPC";W[W.LATENCY_ACTION_GET_CART_RPC]="LATENCY_ACTION_GET_CART_RPC";W[W.LATENCY_ACTION_THUMBNAIL_FETCH]="LATENCY_ACTION_THUMBNAIL_FETCH";W[W.LATENCY_ACTION_ABANDONED_DIRECT_PLAYBACK]="LATENCY_ACTION_ABANDONED_DIRECT_PLAYBACK";W[W.LATENCY_ACTION_SHARE_VIDEO]="LATENCY_ACTION_SHARE_VIDEO";
W[W.LATENCY_ACTION_AD_TO_VIDEO_INT]="LATENCY_ACTION_AD_TO_VIDEO_INT";W[W.LATENCY_ACTION_ABANDONED_BROWSE]="LATENCY_ACTION_ABANDONED_BROWSE";W[W.LATENCY_ACTION_PLAYER_ROTATION]="LATENCY_ACTION_PLAYER_ROTATION";W[W.LATENCY_ACTION_SHOPPING_IN_APP]="LATENCY_ACTION_SHOPPING_IN_APP";W[W.LATENCY_ACTION_PLAYER_ATTESTATION]="LATENCY_ACTION_PLAYER_ATTESTATION";W[W.LATENCY_ACTION_PLAYER_SEEK]="LATENCY_ACTION_PLAYER_SEEK";W[W.LATENCY_ACTION_SUPER_STICKER_BUY_FLOW]="LATENCY_ACTION_SUPER_STICKER_BUY_FLOW";
W[W.LATENCY_ACTION_BLOCKS_PERFORMANCE]="LATENCY_ACTION_BLOCKS_PERFORMANCE";W[W.LATENCY_ACTION_ASSISTANT_QUERY]="LATENCY_ACTION_ASSISTANT_QUERY";W[W.LATENCY_ACTION_ASSISTANT_SETTINGS]="LATENCY_ACTION_ASSISTANT_SETTINGS";W[W.LATENCY_ACTION_ENTITY_KEY_DESERIALIZATION_PERF]="LATENCY_ACTION_ENTITY_KEY_DESERIALIZATION_PERF";W[W.LATENCY_ACTION_ENTITY_KEY_SERIALIZATION_PERF]="LATENCY_ACTION_ENTITY_KEY_SERIALIZATION_PERF";W[W.LATENCY_ACTION_PROOF_OF_ORIGIN_TOKEN_CREATE]="LATENCY_ACTION_PROOF_OF_ORIGIN_TOKEN_CREATE";
W[W.LATENCY_ACTION_EMBEDS_SDK_INITIALIZATION]="LATENCY_ACTION_EMBEDS_SDK_INITIALIZATION";W[W.LATENCY_ACTION_NETWORKLESS_PERFORMANCE]="LATENCY_ACTION_NETWORKLESS_PERFORMANCE";W[W.LATENCY_ACTION_DOWNLOADS_EXPANSION]="LATENCY_ACTION_DOWNLOADS_EXPANSION";W[W.LATENCY_ACTION_ENTITY_TRANSFORM]="LATENCY_ACTION_ENTITY_TRANSFORM";W[W.LATENCY_ACTION_DOWNLOADS_COMPATIBILITY_LAYER]="LATENCY_ACTION_DOWNLOADS_COMPATIBILITY_LAYER";W[W.LATENCY_ACTION_EMBEDS_SET_VIDEO]="LATENCY_ACTION_EMBEDS_SET_VIDEO";
W[W.LATENCY_ACTION_SETTINGS]="LATENCY_ACTION_SETTINGS";W[W.LATENCY_ACTION_ABANDONED_STARTUP]="LATENCY_ACTION_ABANDONED_STARTUP";W[W.LATENCY_ACTION_MEDIA_BROWSER_ALARM_PLAY]="LATENCY_ACTION_MEDIA_BROWSER_ALARM_PLAY";W[W.LATENCY_ACTION_MEDIA_BROWSER_SEARCH]="LATENCY_ACTION_MEDIA_BROWSER_SEARCH";W[W.LATENCY_ACTION_MEDIA_BROWSER_LOAD_TREE]="LATENCY_ACTION_MEDIA_BROWSER_LOAD_TREE";W[W.LATENCY_ACTION_WHO_IS_WATCHING]="LATENCY_ACTION_WHO_IS_WATCHING";W[W.LATENCY_ACTION_CAST_LOAD_BY_ENTITY_TO_WATCH]="LATENCY_ACTION_CAST_LOAD_BY_ENTITY_TO_WATCH";
W[W.LATENCY_ACTION_LITE_SWITCH_ACCOUNT]="LATENCY_ACTION_LITE_SWITCH_ACCOUNT";W[W.LATENCY_ACTION_ELEMENTS_PERFORMANCE]="LATENCY_ACTION_ELEMENTS_PERFORMANCE";W[W.LATENCY_ACTION_LOCATION_SIGNAL_COLLECTION]="LATENCY_ACTION_LOCATION_SIGNAL_COLLECTION";W[W.LATENCY_ACTION_MODIFY_CHANNEL_NOTIFICATION]="LATENCY_ACTION_MODIFY_CHANNEL_NOTIFICATION";W[W.LATENCY_ACTION_OFFLINE_STORE_START]="LATENCY_ACTION_OFFLINE_STORE_START";W[W.LATENCY_ACTION_REEL_EDITOR]="LATENCY_ACTION_REEL_EDITOR";
W[W.LATENCY_ACTION_CHANNEL_SUBSCRIBE]="LATENCY_ACTION_CHANNEL_SUBSCRIBE";W[W.LATENCY_ACTION_CHANNEL_PREVIEW]="LATENCY_ACTION_CHANNEL_PREVIEW";W[W.LATENCY_ACTION_PREFETCH]="LATENCY_ACTION_PREFETCH";W[W.LATENCY_ACTION_ABANDONED_WATCH]="LATENCY_ACTION_ABANDONED_WATCH";W[W.LATENCY_ACTION_LOAD_COMMENT_REPLIES]="LATENCY_ACTION_LOAD_COMMENT_REPLIES";W[W.LATENCY_ACTION_LOAD_COMMENTS]="LATENCY_ACTION_LOAD_COMMENTS";W[W.LATENCY_ACTION_EDIT_COMMENT]="LATENCY_ACTION_EDIT_COMMENT";
W[W.LATENCY_ACTION_NEW_COMMENT]="LATENCY_ACTION_NEW_COMMENT";W[W.LATENCY_ACTION_OFFLINE_SHARING_RECEIVER_PAIRING]="LATENCY_ACTION_OFFLINE_SHARING_RECEIVER_PAIRING";W[W.LATENCY_ACTION_EMBED]="LATENCY_ACTION_EMBED";W[W.LATENCY_ACTION_MDX_LAUNCH]="LATENCY_ACTION_MDX_LAUNCH";W[W.LATENCY_ACTION_RESOLVE_URL]="LATENCY_ACTION_RESOLVE_URL";W[W.LATENCY_ACTION_CAST_SPLASH]="LATENCY_ACTION_CAST_SPLASH";W[W.LATENCY_ACTION_MDX_CAST]="LATENCY_ACTION_MDX_CAST";W[W.LATENCY_ACTION_MDX_COMMAND]="LATENCY_ACTION_MDX_COMMAND";
W[W.LATENCY_ACTION_REEL_SELECT_SEGMENT]="LATENCY_ACTION_REEL_SELECT_SEGMENT";W[W.LATENCY_ACTION_ACCELERATED_EFFECTS]="LATENCY_ACTION_ACCELERATED_EFFECTS";W[W.LATENCY_ACTION_UPLOAD_AUDIO_MIXER]="LATENCY_ACTION_UPLOAD_AUDIO_MIXER";W[W.LATENCY_ACTION_SHORTS_CLIENT_SIDE_RENDERING]="LATENCY_ACTION_SHORTS_CLIENT_SIDE_RENDERING";W[W.LATENCY_ACTION_SHORTS_SEG_IMP_TRANSCODING]="LATENCY_ACTION_SHORTS_SEG_IMP_TRANSCODING";W[W.LATENCY_ACTION_SHORTS_AUDIO_PICKER_PLAYBACK]="LATENCY_ACTION_SHORTS_AUDIO_PICKER_PLAYBACK";
W[W.LATENCY_ACTION_SHORTS_WAVEFORM_DOWNLOAD]="LATENCY_ACTION_SHORTS_WAVEFORM_DOWNLOAD";W[W.LATENCY_ACTION_SHORTS_VIDEO_INGESTION]="LATENCY_ACTION_SHORTS_VIDEO_INGESTION";W[W.LATENCY_ACTION_SHORTS_GALLERY]="LATENCY_ACTION_SHORTS_GALLERY";W[W.LATENCY_ACTION_SHORTS_TRIM]="LATENCY_ACTION_SHORTS_TRIM";W[W.LATENCY_ACTION_SHORTS_EDIT]="LATENCY_ACTION_SHORTS_EDIT";W[W.LATENCY_ACTION_SHORTS_CAMERA]="LATENCY_ACTION_SHORTS_CAMERA";W[W.LATENCY_ACTION_PARENT_TOOLS_DASHBOARD]="LATENCY_ACTION_PARENT_TOOLS_DASHBOARD";
W[W.LATENCY_ACTION_PARENT_TOOLS_COLLECTION]="LATENCY_ACTION_PARENT_TOOLS_COLLECTION";W[W.LATENCY_ACTION_MUSIC_LOAD_RECOMMENDED_MEDIA_ITEMS]="LATENCY_ACTION_MUSIC_LOAD_RECOMMENDED_MEDIA_ITEMS";W[W.LATENCY_ACTION_MUSIC_LOAD_MEDIA_ITEMS]="LATENCY_ACTION_MUSIC_LOAD_MEDIA_ITEMS";W[W.LATENCY_ACTION_MUSIC_ALBUM_DETAIL]="LATENCY_ACTION_MUSIC_ALBUM_DETAIL";W[W.LATENCY_ACTION_MUSIC_PLAYLIST_DETAIL]="LATENCY_ACTION_MUSIC_PLAYLIST_DETAIL";W[W.LATENCY_ACTION_CHIPS]="LATENCY_ACTION_CHIPS";
W[W.LATENCY_ACTION_SEARCH_ZERO_STATE]="LATENCY_ACTION_SEARCH_ZERO_STATE";W[W.LATENCY_ACTION_LIVE_PAGINATION]="LATENCY_ACTION_LIVE_PAGINATION";W[W.LATENCY_ACTION_LIVE]="LATENCY_ACTION_LIVE";W[W.LATENCY_ACTION_PREBUFFER]="LATENCY_ACTION_PREBUFFER";W[W.LATENCY_ACTION_TENX]="LATENCY_ACTION_TENX";W[W.LATENCY_ACTION_KIDS_PROFILE_SETTINGS]="LATENCY_ACTION_KIDS_PROFILE_SETTINGS";W[W.LATENCY_ACTION_KIDS_WATCH_IT_AGAIN]="LATENCY_ACTION_KIDS_WATCH_IT_AGAIN";W[W.LATENCY_ACTION_KIDS_SECRET_CODE]="LATENCY_ACTION_KIDS_SECRET_CODE";
W[W.LATENCY_ACTION_KIDS_PARENT_PROFILE_SETTINGS]="LATENCY_ACTION_KIDS_PARENT_PROFILE_SETTINGS";W[W.LATENCY_ACTION_KIDS_ONBOARDING]="LATENCY_ACTION_KIDS_ONBOARDING";W[W.LATENCY_ACTION_KIDS_VOICE_SEARCH]="LATENCY_ACTION_KIDS_VOICE_SEARCH";W[W.LATENCY_ACTION_KIDS_CURATED_COLLECTION]="LATENCY_ACTION_KIDS_CURATED_COLLECTION";W[W.LATENCY_ACTION_KIDS_LIBRARY]="LATENCY_ACTION_KIDS_LIBRARY";W[W.LATENCY_ACTION_CREATOR_VIDEO_TRANSLATIONS]="LATENCY_ACTION_CREATOR_VIDEO_TRANSLATIONS";
W[W.LATENCY_ACTION_CREATOR_VIDEO_MONETIZATION]="LATENCY_ACTION_CREATOR_VIDEO_MONETIZATION";W[W.LATENCY_ACTION_CREATOR_VIDEO_LIVE_STREAMING]="LATENCY_ACTION_CREATOR_VIDEO_LIVE_STREAMING";W[W.LATENCY_ACTION_CREATOR_VIDEO_LIVE_SETTINGS]="LATENCY_ACTION_CREATOR_VIDEO_LIVE_SETTINGS";W[W.LATENCY_ACTION_CREATOR_VIDEO_EDITOR_ASYNC]="LATENCY_ACTION_CREATOR_VIDEO_EDITOR_ASYNC";W[W.LATENCY_ACTION_CREATOR_VIDEO_EDITOR]="LATENCY_ACTION_CREATOR_VIDEO_EDITOR";W[W.LATENCY_ACTION_CREATOR_VIDEO_EDIT]="LATENCY_ACTION_CREATOR_VIDEO_EDIT";
W[W.LATENCY_ACTION_CREATOR_VIDEO_COMMENTS]="LATENCY_ACTION_CREATOR_VIDEO_COMMENTS";W[W.LATENCY_ACTION_CREATOR_VIDEO_ANALYTICS]="LATENCY_ACTION_CREATOR_VIDEO_ANALYTICS";W[W.LATENCY_ACTION_CREATOR_POST_LIST]="LATENCY_ACTION_CREATOR_POST_LIST";W[W.LATENCY_ACTION_CREATOR_POST_EDIT]="LATENCY_ACTION_CREATOR_POST_EDIT";W[W.LATENCY_ACTION_CREATOR_POST_COMMENTS]="LATENCY_ACTION_CREATOR_POST_COMMENTS";W[W.LATENCY_ACTION_CREATOR_LIVE_STREAMING]="LATENCY_ACTION_CREATOR_LIVE_STREAMING";
W[W.LATENCY_ACTION_CREATOR_DIALOG_VIDEO_COPYRIGHT]="LATENCY_ACTION_CREATOR_DIALOG_VIDEO_COPYRIGHT";W[W.LATENCY_ACTION_CREATOR_DIALOG_UPLOADS]="LATENCY_ACTION_CREATOR_DIALOG_UPLOADS";W[W.LATENCY_ACTION_CREATOR_DIALOG_COPYRIGHT_STRIKES]="LATENCY_ACTION_CREATOR_DIALOG_COPYRIGHT_STRIKES";W[W.LATENCY_ACTION_CREATOR_CHANNEL_VIDEOS]="LATENCY_ACTION_CREATOR_CHANNEL_VIDEOS";W[W.LATENCY_ACTION_CREATOR_CHANNEL_TRANSLATIONS]="LATENCY_ACTION_CREATOR_CHANNEL_TRANSLATIONS";
W[W.LATENCY_ACTION_CREATOR_CHANNEL_PLAYLISTS]="LATENCY_ACTION_CREATOR_CHANNEL_PLAYLISTS";W[W.LATENCY_ACTION_CREATOR_CHANNEL_MUSIC]="LATENCY_ACTION_CREATOR_CHANNEL_MUSIC";W[W.LATENCY_ACTION_CREATOR_CHANNEL_MONETIZATION]="LATENCY_ACTION_CREATOR_CHANNEL_MONETIZATION";W[W.LATENCY_ACTION_CREATOR_CHANNEL_EDITING]="LATENCY_ACTION_CREATOR_CHANNEL_EDITING";W[W.LATENCY_ACTION_CREATOR_CHANNEL_DASHBOARD]="LATENCY_ACTION_CREATOR_CHANNEL_DASHBOARD";W[W.LATENCY_ACTION_CREATOR_CHANNEL_COPYRIGHT]="LATENCY_ACTION_CREATOR_CHANNEL_COPYRIGHT";
W[W.LATENCY_ACTION_CREATOR_CHANNEL_COMMENTS]="LATENCY_ACTION_CREATOR_CHANNEL_COMMENTS";W[W.LATENCY_ACTION_CREATOR_CHANNEL_ANALYTICS]="LATENCY_ACTION_CREATOR_CHANNEL_ANALYTICS";W[W.LATENCY_ACTION_CREATOR_ARTIST_PROFILE]="LATENCY_ACTION_CREATOR_ARTIST_PROFILE";W[W.LATENCY_ACTION_CREATOR_ARTIST_CONCERTS]="LATENCY_ACTION_CREATOR_ARTIST_CONCERTS";W[W.LATENCY_ACTION_CREATOR_ARTIST_ANALYTICS]="LATENCY_ACTION_CREATOR_ARTIST_ANALYTICS";W[W.LATENCY_ACTION_CREATOR_ANALYTICS_EXPLORE]="LATENCY_ACTION_CREATOR_ANALYTICS_EXPLORE";
W[W.LATENCY_ACTION_STORYBOARD_THUMBNAILS]="LATENCY_ACTION_STORYBOARD_THUMBNAILS";W[W.LATENCY_ACTION_SEARCH_THUMBNAILS]="LATENCY_ACTION_SEARCH_THUMBNAILS";W[W.LATENCY_ACTION_ON_DEVICE_MODEL_DOWNLOAD]="LATENCY_ACTION_ON_DEVICE_MODEL_DOWNLOAD";W[W.LATENCY_ACTION_VOICE_ASSISTANT]="LATENCY_ACTION_VOICE_ASSISTANT";W[W.LATENCY_ACTION_SEARCH_UI]="LATENCY_ACTION_SEARCH_UI";W[W.LATENCY_ACTION_SUGGEST]="LATENCY_ACTION_SUGGEST";W[W.LATENCY_ACTION_AUTO_SEARCH]="LATENCY_ACTION_AUTO_SEARCH";
W[W.LATENCY_ACTION_DOWNLOADS]="LATENCY_ACTION_DOWNLOADS";W[W.LATENCY_ACTION_EXPLORE]="LATENCY_ACTION_EXPLORE";W[W.LATENCY_ACTION_VIDEO_LIST]="LATENCY_ACTION_VIDEO_LIST";W[W.LATENCY_ACTION_HOME_RESUME]="LATENCY_ACTION_HOME_RESUME";W[W.LATENCY_ACTION_SUBSCRIPTIONS_LIST]="LATENCY_ACTION_SUBSCRIPTIONS_LIST";W[W.LATENCY_ACTION_THUMBNAIL_LOAD]="LATENCY_ACTION_THUMBNAIL_LOAD";W[W.LATENCY_ACTION_FIRST_THUMBNAIL_LOAD]="LATENCY_ACTION_FIRST_THUMBNAIL_LOAD";W[W.LATENCY_ACTION_SUBSCRIPTIONS_FEED]="LATENCY_ACTION_SUBSCRIPTIONS_FEED";
W[W.LATENCY_ACTION_SUBSCRIPTIONS]="LATENCY_ACTION_SUBSCRIPTIONS";W[W.LATENCY_ACTION_TRENDING]="LATENCY_ACTION_TRENDING";W[W.LATENCY_ACTION_LIBRARY]="LATENCY_ACTION_LIBRARY";W[W.LATENCY_ACTION_VIDEO_THUMBNAIL]="LATENCY_ACTION_VIDEO_THUMBNAIL";W[W.LATENCY_ACTION_SHOW_MORE]="LATENCY_ACTION_SHOW_MORE";W[W.LATENCY_ACTION_VIDEO_PREVIEW]="LATENCY_ACTION_VIDEO_PREVIEW";W[W.LATENCY_ACTION_PREBUFFER_VIDEO]="LATENCY_ACTION_PREBUFFER_VIDEO";W[W.LATENCY_ACTION_PREFETCH_VIDEO]="LATENCY_ACTION_PREFETCH_VIDEO";
W[W.LATENCY_ACTION_DIRECT_PLAYBACK]="LATENCY_ACTION_DIRECT_PLAYBACK";W[W.LATENCY_ACTION_REEL_WATCH]="LATENCY_ACTION_REEL_WATCH";W[W.LATENCY_ACTION_AD_TO_AD]="LATENCY_ACTION_AD_TO_AD";W[W.LATENCY_ACTION_VIDEO_TO_AD]="LATENCY_ACTION_VIDEO_TO_AD";W[W.LATENCY_ACTION_AD_TO_VIDEO]="LATENCY_ACTION_AD_TO_VIDEO";W[W.LATENCY_ACTION_ONBOARDING]="LATENCY_ACTION_ONBOARDING";W[W.LATENCY_ACTION_LOGIN]="LATENCY_ACTION_LOGIN";W[W.LATENCY_ACTION_BROWSE]="LATENCY_ACTION_BROWSE";W[W.LATENCY_ACTION_CHANNELS]="LATENCY_ACTION_CHANNELS";
W[W.LATENCY_ACTION_WATCH]="LATENCY_ACTION_WATCH";W[W.LATENCY_ACTION_RESULTS]="LATENCY_ACTION_RESULTS";W[W.LATENCY_ACTION_HOME]="LATENCY_ACTION_HOME";W[W.LATENCY_ACTION_STARTUP]="LATENCY_ACTION_STARTUP";W[W.LATENCY_ACTION_UNKNOWN]="LATENCY_ACTION_UNKNOWN";var Dr={LATENCY_NETWORK_MOBILE:2,LATENCY_NETWORK_WIFI:1,LATENCY_NETWORK_UNKNOWN:0};Dr[Dr.LATENCY_NETWORK_MOBILE]="LATENCY_NETWORK_MOBILE";Dr[Dr.LATENCY_NETWORK_WIFI]="LATENCY_NETWORK_WIFI";Dr[Dr.LATENCY_NETWORK_UNKNOWN]="LATENCY_NETWORK_UNKNOWN";
var X={CONN_INVALID:31,CONN_CELLULAR_5G_NSA:12,CONN_CELLULAR_5G_SA:11,CONN_WIFI_METERED:10,CONN_CELLULAR_5G:9,CONN_DISCO:8,CONN_CELLULAR_UNKNOWN:7,CONN_CELLULAR_4G:6,CONN_CELLULAR_3G:5,CONN_CELLULAR_2G:4,CONN_WIFI:3,CONN_NONE:2,CONN_UNKNOWN:1,CONN_DEFAULT:0};X[X.CONN_INVALID]="CONN_INVALID";X[X.CONN_CELLULAR_5G_NSA]="CONN_CELLULAR_5G_NSA";X[X.CONN_CELLULAR_5G_SA]="CONN_CELLULAR_5G_SA";X[X.CONN_WIFI_METERED]="CONN_WIFI_METERED";X[X.CONN_CELLULAR_5G]="CONN_CELLULAR_5G";X[X.CONN_DISCO]="CONN_DISCO";
X[X.CONN_CELLULAR_UNKNOWN]="CONN_CELLULAR_UNKNOWN";X[X.CONN_CELLULAR_4G]="CONN_CELLULAR_4G";X[X.CONN_CELLULAR_3G]="CONN_CELLULAR_3G";X[X.CONN_CELLULAR_2G]="CONN_CELLULAR_2G";X[X.CONN_WIFI]="CONN_WIFI";X[X.CONN_NONE]="CONN_NONE";X[X.CONN_UNKNOWN]="CONN_UNKNOWN";X[X.CONN_DEFAULT]="CONN_DEFAULT";
var Y={DETAILED_NETWORK_TYPE_NR_NSA:126,DETAILED_NETWORK_TYPE_NR_SA:125,DETAILED_NETWORK_TYPE_INTERNAL_WIFI_IMPAIRED:124,DETAILED_NETWORK_TYPE_APP_WIFI_HOTSPOT:123,DETAILED_NETWORK_TYPE_DISCONNECTED:122,DETAILED_NETWORK_TYPE_NON_MOBILE_UNKNOWN:121,DETAILED_NETWORK_TYPE_MOBILE_UNKNOWN:120,DETAILED_NETWORK_TYPE_WIMAX:119,DETAILED_NETWORK_TYPE_ETHERNET:118,DETAILED_NETWORK_TYPE_BLUETOOTH:117,DETAILED_NETWORK_TYPE_WIFI:116,DETAILED_NETWORK_TYPE_LTE:115,DETAILED_NETWORK_TYPE_HSPAP:114,DETAILED_NETWORK_TYPE_EHRPD:113,
DETAILED_NETWORK_TYPE_EVDO_B:112,DETAILED_NETWORK_TYPE_UMTS:111,DETAILED_NETWORK_TYPE_IDEN:110,DETAILED_NETWORK_TYPE_HSUPA:109,DETAILED_NETWORK_TYPE_HSPA:108,DETAILED_NETWORK_TYPE_HSDPA:107,DETAILED_NETWORK_TYPE_EVDO_A:106,DETAILED_NETWORK_TYPE_EVDO_0:105,DETAILED_NETWORK_TYPE_CDMA:104,DETAILED_NETWORK_TYPE_1_X_RTT:103,DETAILED_NETWORK_TYPE_GPRS:102,DETAILED_NETWORK_TYPE_EDGE:101,DETAILED_NETWORK_TYPE_UNKNOWN:0};Y[Y.DETAILED_NETWORK_TYPE_NR_NSA]="DETAILED_NETWORK_TYPE_NR_NSA";
Y[Y.DETAILED_NETWORK_TYPE_NR_SA]="DETAILED_NETWORK_TYPE_NR_SA";Y[Y.DETAILED_NETWORK_TYPE_INTERNAL_WIFI_IMPAIRED]="DETAILED_NETWORK_TYPE_INTERNAL_WIFI_IMPAIRED";Y[Y.DETAILED_NETWORK_TYPE_APP_WIFI_HOTSPOT]="DETAILED_NETWORK_TYPE_APP_WIFI_HOTSPOT";Y[Y.DETAILED_NETWORK_TYPE_DISCONNECTED]="DETAILED_NETWORK_TYPE_DISCONNECTED";Y[Y.DETAILED_NETWORK_TYPE_NON_MOBILE_UNKNOWN]="DETAILED_NETWORK_TYPE_NON_MOBILE_UNKNOWN";Y[Y.DETAILED_NETWORK_TYPE_MOBILE_UNKNOWN]="DETAILED_NETWORK_TYPE_MOBILE_UNKNOWN";
Y[Y.DETAILED_NETWORK_TYPE_WIMAX]="DETAILED_NETWORK_TYPE_WIMAX";Y[Y.DETAILED_NETWORK_TYPE_ETHERNET]="DETAILED_NETWORK_TYPE_ETHERNET";Y[Y.DETAILED_NETWORK_TYPE_BLUETOOTH]="DETAILED_NETWORK_TYPE_BLUETOOTH";Y[Y.DETAILED_NETWORK_TYPE_WIFI]="DETAILED_NETWORK_TYPE_WIFI";Y[Y.DETAILED_NETWORK_TYPE_LTE]="DETAILED_NETWORK_TYPE_LTE";Y[Y.DETAILED_NETWORK_TYPE_HSPAP]="DETAILED_NETWORK_TYPE_HSPAP";Y[Y.DETAILED_NETWORK_TYPE_EHRPD]="DETAILED_NETWORK_TYPE_EHRPD";Y[Y.DETAILED_NETWORK_TYPE_EVDO_B]="DETAILED_NETWORK_TYPE_EVDO_B";
Y[Y.DETAILED_NETWORK_TYPE_UMTS]="DETAILED_NETWORK_TYPE_UMTS";Y[Y.DETAILED_NETWORK_TYPE_IDEN]="DETAILED_NETWORK_TYPE_IDEN";Y[Y.DETAILED_NETWORK_TYPE_HSUPA]="DETAILED_NETWORK_TYPE_HSUPA";Y[Y.DETAILED_NETWORK_TYPE_HSPA]="DETAILED_NETWORK_TYPE_HSPA";Y[Y.DETAILED_NETWORK_TYPE_HSDPA]="DETAILED_NETWORK_TYPE_HSDPA";Y[Y.DETAILED_NETWORK_TYPE_EVDO_A]="DETAILED_NETWORK_TYPE_EVDO_A";Y[Y.DETAILED_NETWORK_TYPE_EVDO_0]="DETAILED_NETWORK_TYPE_EVDO_0";Y[Y.DETAILED_NETWORK_TYPE_CDMA]="DETAILED_NETWORK_TYPE_CDMA";
Y[Y.DETAILED_NETWORK_TYPE_1_X_RTT]="DETAILED_NETWORK_TYPE_1_X_RTT";Y[Y.DETAILED_NETWORK_TYPE_GPRS]="DETAILED_NETWORK_TYPE_GPRS";Y[Y.DETAILED_NETWORK_TYPE_EDGE]="DETAILED_NETWORK_TYPE_EDGE";Y[Y.DETAILED_NETWORK_TYPE_UNKNOWN]="DETAILED_NETWORK_TYPE_UNKNOWN";var Er={LATENCY_PLAYER_RTSP:7,LATENCY_PLAYER_HTML5_INLINE:6,LATENCY_PLAYER_HTML5_FULLSCREEN:5,LATENCY_PLAYER_HTML5:4,LATENCY_PLAYER_FRAMEWORK:3,LATENCY_PLAYER_FLASH:2,LATENCY_PLAYER_EXO:1,LATENCY_PLAYER_UNKNOWN:0};Er[Er.LATENCY_PLAYER_RTSP]="LATENCY_PLAYER_RTSP";
Er[Er.LATENCY_PLAYER_HTML5_INLINE]="LATENCY_PLAYER_HTML5_INLINE";Er[Er.LATENCY_PLAYER_HTML5_FULLSCREEN]="LATENCY_PLAYER_HTML5_FULLSCREEN";Er[Er.LATENCY_PLAYER_HTML5]="LATENCY_PLAYER_HTML5";Er[Er.LATENCY_PLAYER_FRAMEWORK]="LATENCY_PLAYER_FRAMEWORK";Er[Er.LATENCY_PLAYER_FLASH]="LATENCY_PLAYER_FLASH";Er[Er.LATENCY_PLAYER_EXO]="LATENCY_PLAYER_EXO";Er[Er.LATENCY_PLAYER_UNKNOWN]="LATENCY_PLAYER_UNKNOWN";
var Fr={LATENCY_AD_BREAK_TYPE_POSTROLL:3,LATENCY_AD_BREAK_TYPE_MIDROLL:2,LATENCY_AD_BREAK_TYPE_PREROLL:1,LATENCY_AD_BREAK_TYPE_UNKNOWN:0};Fr[Fr.LATENCY_AD_BREAK_TYPE_POSTROLL]="LATENCY_AD_BREAK_TYPE_POSTROLL";Fr[Fr.LATENCY_AD_BREAK_TYPE_MIDROLL]="LATENCY_AD_BREAK_TYPE_MIDROLL";Fr[Fr.LATENCY_AD_BREAK_TYPE_PREROLL]="LATENCY_AD_BREAK_TYPE_PREROLL";Fr[Fr.LATENCY_AD_BREAK_TYPE_UNKNOWN]="LATENCY_AD_BREAK_TYPE_UNKNOWN";var Gr={LATENCY_ACTION_ERROR_STARTUP_TIMEOUT:1,LATENCY_ACTION_ERROR_UNSPECIFIED:0};
Gr[Gr.LATENCY_ACTION_ERROR_STARTUP_TIMEOUT]="LATENCY_ACTION_ERROR_STARTUP_TIMEOUT";Gr[Gr.LATENCY_ACTION_ERROR_UNSPECIFIED]="LATENCY_ACTION_ERROR_UNSPECIFIED";var Hr={LIVE_STREAM_MODE_WINDOW:5,LIVE_STREAM_MODE_POST:4,LIVE_STREAM_MODE_LP:3,LIVE_STREAM_MODE_LIVE:2,LIVE_STREAM_MODE_DVR:1,LIVE_STREAM_MODE_UNKNOWN:0};Hr[Hr.LIVE_STREAM_MODE_WINDOW]="LIVE_STREAM_MODE_WINDOW";Hr[Hr.LIVE_STREAM_MODE_POST]="LIVE_STREAM_MODE_POST";Hr[Hr.LIVE_STREAM_MODE_LP]="LIVE_STREAM_MODE_LP";
Hr[Hr.LIVE_STREAM_MODE_LIVE]="LIVE_STREAM_MODE_LIVE";Hr[Hr.LIVE_STREAM_MODE_DVR]="LIVE_STREAM_MODE_DVR";Hr[Hr.LIVE_STREAM_MODE_UNKNOWN]="LIVE_STREAM_MODE_UNKNOWN";var Ir={VIDEO_STREAM_TYPE_VOD:3,VIDEO_STREAM_TYPE_DVR:2,VIDEO_STREAM_TYPE_LIVE:1,VIDEO_STREAM_TYPE_UNSPECIFIED:0};Ir[Ir.VIDEO_STREAM_TYPE_VOD]="VIDEO_STREAM_TYPE_VOD";Ir[Ir.VIDEO_STREAM_TYPE_DVR]="VIDEO_STREAM_TYPE_DVR";Ir[Ir.VIDEO_STREAM_TYPE_LIVE]="VIDEO_STREAM_TYPE_LIVE";Ir[Ir.VIDEO_STREAM_TYPE_UNSPECIFIED]="VIDEO_STREAM_TYPE_UNSPECIFIED";
var Jr={YT_IDB_TRANSACTION_TYPE_READ:2,YT_IDB_TRANSACTION_TYPE_WRITE:1,YT_IDB_TRANSACTION_TYPE_UNKNOWN:0};Jr[Jr.YT_IDB_TRANSACTION_TYPE_READ]="YT_IDB_TRANSACTION_TYPE_READ";Jr[Jr.YT_IDB_TRANSACTION_TYPE_WRITE]="YT_IDB_TRANSACTION_TYPE_WRITE";Jr[Jr.YT_IDB_TRANSACTION_TYPE_UNKNOWN]="YT_IDB_TRANSACTION_TYPE_UNKNOWN";var Kr={PLAYER_ROTATION_TYPE_PORTRAIT_TO_FULLSCREEN:2,PLAYER_ROTATION_TYPE_FULLSCREEN_TO_PORTRAIT:1,PLAYER_ROTATION_TYPE_UNKNOWN:0};Kr[Kr.PLAYER_ROTATION_TYPE_PORTRAIT_TO_FULLSCREEN]="PLAYER_ROTATION_TYPE_PORTRAIT_TO_FULLSCREEN";
Kr[Kr.PLAYER_ROTATION_TYPE_FULLSCREEN_TO_PORTRAIT]="PLAYER_ROTATION_TYPE_FULLSCREEN_TO_PORTRAIT";Kr[Kr.PLAYER_ROTATION_TYPE_UNKNOWN]="PLAYER_ROTATION_TYPE_UNKNOWN";var Lr="actionVisualElement spinnerInfo resourceInfo playerInfo commentInfo mdxInfo watchInfo thumbnailLoadInfo creatorInfo unpluggedInfo reelInfo subscriptionsFeedInfo requestIds mediaBrowserActionInfo musicLoadActionInfo shoppingInfo prefetchInfo accelerationSession webInfo tvInfo kabukiInfo mwebInfo musicInfo".split(" ");var Mr=y.ytLoggingLatencyUsageStats_||{};z("ytLoggingLatencyUsageStats_",Mr);function Nr(){this.i=0}
function Or(){Nr.i||(Nr.i=new Nr);return Nr.i}
Nr.prototype.tick=function(a,b,c,d){Pr(this,"tick_"+a+"_"+b)||(c={timestamp:c,cttAuthInfo:d},M("web_csi_via_jspb")?(d=new wh,C(d,1,a),C(d,2,b),a=new zh,pd(a,5,Ah,d),qp(a,c)):Yj("latencyActionTicked",{tickName:a,clientActionNonce:b},c))};
Nr.prototype.info=function(a,b,c){var d=Object.keys(a).join("");Pr(this,"info_"+d+"_"+b)||(a=Object.assign({},a),a.clientActionNonce=b,Yj("latencyActionInfo",a,{cttAuthInfo:c}))};
Nr.prototype.jspbInfo=function(a,b,c){for(var d="",e=0;e<a.toJSON().length;e++)void 0!==a.toJSON()[e]&&(d=0===e?d.concat(""+e):d.concat("_"+e));Pr(this,"info_"+d+"_"+b)||(C(a,2,b),b={cttAuthInfo:c},c=new zh,pd(c,7,Ah,a),qp(c,b))};
Nr.prototype.span=function(a,b,c){var d=Object.keys(a).join("");Pr(this,"span_"+d+"_"+b)||(a.clientActionNonce=b,Yj("latencyActionSpan",a,{cttAuthInfo:c}))};
function Pr(a,b){Mr[b]=Mr[b]||{count:0};var c=Mr[b];c.count++;c.time=P();a.i||(a.i=Ij(function(){var d=P(),e;for(e in Mr)Mr[e]&&6E4<d-Mr[e].time&&delete Mr[e];a&&(a.i=0)},5E3));
return 5<c.count?(6===c.count&&1>1E5*Math.random()&&(c=new Q("CSI data exceeded logging limit with key",b.split("_")),0<=b.indexOf("plev")||Ep(c)),!0):!1}
;function Qr(){var a=["ol"];ur("").info.actionType="embed";a&&Mh("TIMING_AFT_KEYS",a);Mh("TIMING_ACTION","embed");Rr();a=mr();var b=or();if("cold"===a.yt_lt||"cold"===b.loadType){var c=hr(),d=nr();d=d.gelTicks?d.gelTicks:d.gelTicks={};for(var e in c)e in d||Z(e,c[e]);e={};c=!1;d=q(Object.keys(a));for(var f=d.next();!f.done;f=d.next())f=f.value,(f=Cr(f,a[f]))&&!rr(or(),f)&&(Wq(b,f),Wq(e,f),c=!0);c&&Sr(e)}z("ytglobal.timingready_",!0);a=L("TIMING_ACTION");A("ytglobal.timingready_")&&a&&"_start"in hr()&&
gr()&&qr()}
function Tr(a,b,c,d){null!==b&&(mr(c)[a]=b,(a=Cr(a,b,c))&&Sr(a,c,d))}
function Sr(a,b,c){if(!M("web_csi_via_jspb")||(void 0===c?0:c))c=ur(b||""),Wq(c.info,a),Wq(or(b),a),c=pr(b),b=lr(b).cttAuthInfo,Or().info(a,c,b);else{c=new qh;var d=Object.keys(a);a=Object.values(a);for(var e=0;e<d.length;e++){var f=d[e];try{switch(f){case "actionType":sh(c,W[a[e]]);break;case "clientActionNonce":C(c,2,a[e]);break;case "clientScreenNonce":C(c,4,a[e]);break;case "loadType":C(c,3,a[e]);break;case "isPrewarmedLaunch":C(c,92,a[e]);break;case "isFirstInstall":C(c,55,a[e]);break;case "networkType":C(c,
5,Dr[a[e]]);break;case "connectionType":C(c,26,X[a[e]]);break;case "detailedConnectionType":C(c,27,Y[a[e]]);break;case "isVisible":C(c,6,a[e]);break;case "playerType":C(c,7,Er[a[e]]);break;case "clientPlaybackNonce":C(c,8,a[e]);break;case "adClientPlaybackNonce":C(c,28,a[e]);break;case "previousCpn":C(c,77,a[e]);break;case "targetCpn":C(c,76,a[e]);break;case "isMonetized":C(c,9,a[e]);break;case "isPrerollAllowed":C(c,16,a[e]);break;case "isPrerollShown":C(c,17,a[e]);break;case "adType":C(c,12,a[e]);
break;case "adTypesAllowed":C(c,36,a[e]);break;case "adNetworks":C(c,37,a[e]);break;case "previousAction":C(c,13,W[a[e]]);break;case "isRedSubscriber":C(c,14,a[e]);break;case "serverTimeMs":C(c,15,a[e]);break;case "videoId":c.setVideoId(a[e]);break;case "adVideoId":C(c,20,a[e]);break;case "targetVideoId":C(c,78,a[e]);break;case "adBreakType":C(c,21,Fr[a[e]]);break;case "isNavigation":th(c,a[e]);break;case "viewportHeight":C(c,29,a[e]);break;case "viewportWidth":C(c,30,a[e]);break;case "screenHeight":C(c,
84,a[e]);break;case "screenWidth":C(c,85,a[e]);break;case "browseId":C(c,31,a[e]);break;case "isCacheHit":C(c,32,a[e]);break;case "httpProtocol":C(c,33,a[e]);break;case "transportProtocol":C(c,34,a[e]);break;case "searchQuery":C(c,41,a[e]);break;case "isContinuation":C(c,42,a[e]);break;case "availableProcessors":C(c,43,a[e]);break;case "sdk":C(c,44,a[e]);break;case "isLocalStream":C(c,45,a[e]);break;case "navigationRequestedSameUrl":C(c,64,a[e]);break;case "shellStartupDurationMs":C(c,70,a[e]);break;
case "appInstallDataAgeMs":C(c,73,a[e]);break;case "latencyActionError":C(c,71,Gr[a[e]]);break;case "actionStep":C(c,79,a[e]);break;case "jsHeapSizeLimit":C(c,80,a[e]);break;case "totalJsHeapSize":C(c,81,a[e]);break;case "usedJsHeapSize":C(c,82,a[e]);break;case "sourceVideoDurationMs":C(c,90,a[e]);break;case "videoOutputFrames":C(c,93,a[e]);break;case "adPrebufferedTimeSecs":C(c,39,a[e]);break;case "isLivestream":C(c,47,a[e]);break;case "liveStreamMode":C(c,91,Hr[a[e]]);break;case "adCpn2":C(c,48,
a[e]);break;case "adDaiDriftMillis":C(c,49,a[e]);break;case "videoStreamType":C(c,53,Ir[a[e]]);break;case "playbackRequiresTap":C(c,56,a[e]);break;case "performanceNavigationTiming":C(c,67,a[e]);break;case "transactionType":C(c,74,Jr[a[e]]);break;case "playerRotationType":C(c,101,Kr[a[e]]);break;case "allowedPreroll":C(c,10,a[e]);break;case "shownPreroll":C(c,11,a[e]);break;case "getHomeRequestId":C(c,57,a[e]);break;case "getSearchRequestId":C(c,60,a[e]);break;case "getPlayerRequestId":C(c,61,a[e]);
break;case "getWatchNextRequestId":C(c,62,a[e]);break;case "getBrowseRequestId":C(c,63,a[e]);break;case "getLibraryRequestId":C(c,66,a[e]);break;default:Lr.includes(f)&&Vh(new Q("Codegen laipb translator asked to translate message field",""+f))}}catch(g){Vh(Error("Codegen laipb translator failed to set "+f))}}Ur(c,b)}}
function Ur(a,b){var c=nr(b);c.jspbInfos||(c.jspbInfos=[]);c.jspbInfos.push(a);ur(b||"").jspbInfo.push(a);c=pr(b);b=lr(b).cttAuthInfo;Or().jspbInfo(a,c,b)}
function Z(a,b,c){if(!b&&"_"!==a[0]){var d=a;T.mark&&(0==d.lastIndexOf("mark_",0)||(d="mark_"+d),c&&(d+=" ("+c+")"),T.mark(d))}ur(c||"").tick[a]=b||P();d=nr(c);d.gelTicks&&(d.gelTicks[a]=!0);d=hr(c);var e=b||P();d[a]=e;e=pr(c);var f=lr(c).cttAuthInfo;if("_start"===a){var g=Or();Pr(g,"baseline_"+e)||(b={timestamp:b,cttAuthInfo:f},M("web_csi_via_jspb")?(f=new oh,C(f,1,e),e=new zh,pd(e,6,Ah,f),qp(e,b)):Yj("latencyActionBaselined",{clientActionNonce:e},b))}else Or().tick(a,e,b,f);qr(c);return d[a]}
function Vr(){var a=pr();requestAnimationFrame(function(){setTimeout(function(){a===pr()&&Z("ol",void 0,void 0)},0)})}
function Wr(){var a=document;if("visibilityState"in a)a=a.visibilityState;else{var b=hn+"VisibilityState";a=b in a?a[b]:void 0}switch(a){case "hidden":return 0;case "visible":return 1;case "prerender":return 2;case "unloaded":return 3;default:return-1}}
function Rr(){function a(f){var g=ir(),h=kr();h&&(Z("srt",g.responseStart),1!==f.prerender&&Z("_start",h,void 0));f=sr();0<f&&Z("fpt",f);f=ir();f.isPerformanceNavigationTiming&&Sr({performanceNavigationTiming:!0});Z("nreqs",f.requestStart,void 0);Z("nress",f.responseStart,void 0);Z("nrese",f.responseEnd,void 0);0<f.redirectEnd-f.redirectStart&&(Z("nrs",f.redirectStart,void 0),Z("nre",f.redirectEnd,void 0));0<f.domainLookupEnd-f.domainLookupStart&&(Z("ndnss",f.domainLookupStart,void 0),Z("ndnse",f.domainLookupEnd,
void 0));0<f.connectEnd-f.connectStart&&(Z("ntcps",f.connectStart,void 0),Z("ntcpe",f.connectEnd,void 0));f.secureConnectionStart>=kr()&&0<f.connectEnd-f.secureConnectionStart&&(Z("nstcps",f.secureConnectionStart,void 0),Z("ntcpe",f.connectEnd,void 0));T&&"getEntriesByType"in T&&Xr()}
var b=L("TIMING_INFO",{});if(M("web_csi_via_jspb")){b=Yr(b);Ur(b);b=sh(th(new qh,!0),W[Br(L("TIMING_ACTION"))]);var c=L("PREVIOUS_ACTION");c&&C(b,13,W[Br(c)]);(c=L("CLIENT_PROTOCOL"))&&C(b,33,c);(c=L("CLIENT_TRANSPORT"))&&C(b,34,c);(c=Wp())&&"UNDEFINED_CSN"!==c&&C(b,4,c);c=Wr();1!==c&&-1!==c||C(b,6,!0);c=mr();C(b,3,"cold");a(c);c=Zr();if(0<c.length){c=q(c);for(var d=c.next();!d.done;d=c.next()){d=d.value;var e=new ph;C(e,1,d);qd(b,83,ph,e)}}Ur(b)}else{for(c in b)b.hasOwnProperty(c)&&Tr(c,b[c]);b=
{isNavigation:!0,actionType:Br(L("TIMING_ACTION"))};if(c=L("PREVIOUS_ACTION"))b.previousAction=Br(c);if(c=L("CLIENT_PROTOCOL"))b.httpProtocol=c;if(c=L("CLIENT_TRANSPORT"))b.transportProtocol=c;(c=Wp())&&"UNDEFINED_CSN"!==c&&(b.clientScreenNonce=c);c=Wr();if(1===c||-1===c)b.isVisible=!0;c=mr();b.loadType="cold";a(c);c=Zr();if(0<c.length)for(b.resourceInfo=[],c=q(c),d=c.next();!d.done;d=c.next())b.resourceInfo.push({resourceCache:d.value});Sr(b)}}
function Yr(a){var b=new qh;a=q(Object.entries(a));for(var c=a.next();!c.done;c=a.next()){var d=q(c.value);c=d.next().value;d=d.next().value;switch(c){case "GetBrowse_rid":var e=new vh;C(e,1,c);C(e,2,String(d));uh(b,e);break;case "GetGuide_rid":e=new vh;C(e,1,c);C(e,2,String(d));uh(b,e);break;case "GetHome_rid":e=new vh;C(e,1,c);C(e,2,String(d));uh(b,e);break;case "GetPlayer_rid":e=new vh;C(e,1,c);C(e,2,String(d));uh(b,e);break;case "GetSearch_rid":e=new vh;C(e,1,c);C(e,2,String(d));uh(b,e);break;
case "GetSettings_rid":e=new vh;C(e,1,c);C(e,2,String(d));uh(b,e);break;case "GetTrending_rid":e=new vh;C(e,1,c);C(e,2,String(d));uh(b,e);break;case "GetWatchNext_rid":e=new vh;C(e,1,c);C(e,2,String(d));uh(b,e);break;case "yt_red":C(b,14,!!d);break;case "yt_ad":C(b,9,!!d)}}return b}
function $r(a,b){a=document.querySelector(a);if(!a)return!1;var c="",d=a.nodeName;"SCRIPT"===d?(c=a.src,c||(c=a.getAttribute("data-timing-href"))&&(c=window.location.protocol+c)):"LINK"===d&&(c=a.href);Xb()&&a.setAttribute("nonce",Xb());return c?(a=T.getEntriesByName(c))&&a[0]&&(a=a[0],c=kr(),Z("rsf_"+b,c+Math.round(a.fetchStart)),Z("rse_"+b,c+Math.round(a.responseEnd)),void 0!==a.transferSize&&0===a.transferSize)?!0:!1:!1}
function Zr(){var a=[];if(document.querySelector&&T&&T.getEntriesByName)for(var b in fr)if(fr.hasOwnProperty(b)){var c=fr[b];$r(b,c)&&a.push(c)}return a}
function Xr(){var a=window.location.protocol,b=T.getEntriesByType("resource");b=fb(b,function(c){return 0===c.name.indexOf(a+"//fonts.gstatic.com/s/")});
(b=hb(b,function(c,d){return d.duration>c.duration?d:c},{duration:0}))&&0<b.startTime&&0<b.responseEnd&&(Z("wffs",jr(b.startTime)),Z("wffe",jr(b.responseEnd)))}
var as=window;as.ytcsi&&(as.ytcsi.info=Tr,as.ytcsi.tick=Z);var bs="tokens consistency mss client_location entities store".split(" ");function cs(a,b,c,d){this.s=a;this.J=b;this.m=c;this.l=d;this.j=void 0;this.i=new Map;a.xa||(a.xa={});a.xa=Object.assign({},Vq,a.xa)}
function ds(a,b,c,d){if(void 0!==cs.i){if(d=cs.i,a=[a!==d.s,b!==d.J,c!==d.m,!1,!1,void 0!==d.j],a.some(function(e){return e}))throw new Q("InnerTubeTransportService is already initialized",a);
}else cs.i=new cs(a,b,c,d)}
function es(a){var b={signalServiceEndpoint:{signal:"GET_DATASYNC_IDS"}};var c=void 0===c?Eq:c;var d=Nq(b,a.s);if(!d)return $f(new Q("Error: No request builder found for command.",b));var e=d.s(b,void 0,c);return e?new Vf(function(f){var g,h,k;return w(function(m){if(1==m.i){h="cors"===(null==(g=e.oa)?void 0:g.mode)?"cors":void 0;if(a.m.xc){var p=e.config,u;p=null==p?void 0:null==(u=p.Ea)?void 0:u.sessionIndex;u=vq({sessionIndex:p});k=Object.assign({},fs(h),u);return m.u(2)}return t(m,gs(e.config,
h),3)}2!=m.i&&(k=m.j);f(hs(a,e,k));m.i=0})}):$f(new Q("Error: Failed to build request for command.",b))}
function hs(a,b,c){var d,e,f,g,h,k,m,p,u,x,v,E,G,H,R,N,S,ja;return w(function(O){switch(O.i){case 1:O.u(2);break;case 3:if((d=O.j)&&!d.isExpired())return O.return(Promise.resolve(d.i()));case 2:if(null==(e=b)?0:null==(f=e.Z)?0:f.context)for(g=q([]),h=g.next();!h.done;h=g.next())k=h.value,k.rp(b.Z.context);if(null==(m=a.j)?0:m.wp(b.input,b.Z))return O.return(a.j.mp(b.input,b.Z));(x=null==(u=b.config)?void 0:u.tp)&&a.i.has(x)&&M("web_memoize_inflight_requests")?p=a.i.get(x):(v=JSON.stringify(b.Z),b.oa=
Object.assign({},b.oa,{headers:c}),E=Object.assign({},b.oa),"POST"===b.oa.method&&(E=Object.assign({},E,{body:v})),(null==(G=b.config)?0:G.ic)&&Z(b.config.ic),p=a.J.fetch(b.input,E,b.config),x&&a.i.set(x,p));return t(O,p,4);case 4:H=O.j;x&&a.i.has(x)&&a.i.delete(x);(null==(R=b.config)?0:R.jc)&&Z(b.config.jc);if(H||null==(N=a.j)||!N.hp(b.input,b.Z)){O.u(5);break}return t(O,a.j.lp(b.input,b.Z),6);case 6:H=O.j;case 5:if(H&&a.l)for(S=q(bs),h=S.next();!h.done;h=S.next())ja=h.value,a.l[ja]&&a.l[ja].handleResponse(H,
b);return O.return(H)}})}
function gs(a,b){var c,d,e,f;return w(function(g){if(1==g.i){e=null==(c=a)?void 0:null==(d=c.Ea)?void 0:d.sessionIndex;var h=vq({sessionIndex:e});if(!(h instanceof Vf)){var k=new Vf(bb);Wf(k,2,h);h=k}return t(g,h,2)}f=g.j;return g.return(Promise.resolve(Object.assign({},fs(b),f)))})}
function fs(a){var b={"Content-Type":"application/json"};M("enable_web_eom_visitor_data")&&L("EOM_VISITOR_DATA")?b["X-Goog-EOM-Visitor-Id"]=L("EOM_VISITOR_DATA"):L("VISITOR_DATA")&&(b["X-Goog-Visitor-Id"]=L("VISITOR_DATA"));M("track_webfe_innertube_auth_mismatch")&&(b["X-Youtube-Bootstrap-Logged-In"]=L("LOGGED_IN",!1));"cors"!==a&&((a=L("INNERTUBE_CONTEXT_CLIENT_NAME"))&&(b["X-Youtube-Client-Name"]=a),(a=L("INNERTUBE_CONTEXT_CLIENT_VERSION"))&&(b["X-Youtube-Client-Version"]=a),(a=L("CHROME_CONNECTED_HEADER"))&&
(b["X-Youtube-Chrome-Connected"]=a),M("forward_domain_admin_state_on_embeds")&&(a=L("DOMAIN_ADMIN_STATE"))&&(b["X-Youtube-Domain-Admin-State"]=a));return b}
;var is=["share/get_web_player_share_panel"],js=["feedback"],ks=["notification/modify_channel_preference"],ls=["browse/edit_playlist"],ms=["subscription/subscribe"],ns=["subscription/unsubscribe"];function os(){}
r(os,Sq);os.prototype.l=function(){return ms};
os.prototype.i=function(a){return a.subscribeEndpoint};
os.prototype.j=function(a,b,c){c=void 0===c?{}:c;b.channelIds&&(a.channelIds=b.channelIds);b.siloName&&(a.siloName=b.siloName);b.params&&(a.params=b.params);c.botguardResponse&&(a.botguardResponse=c.botguardResponse);c.feature&&(a.clientFeature=c.feature)};
da.Object.defineProperties(os.prototype,{m:{configurable:!0,enumerable:!0,get:function(){return!0}}});function ps(){}
r(ps,Sq);ps.prototype.l=function(){return ns};
ps.prototype.i=function(a){return a.unsubscribeEndpoint};
ps.prototype.j=function(a,b){b.channelIds&&(a.channelIds=b.channelIds);b.siloName&&(a.siloName=b.siloName);b.params&&(a.params=b.params)};
da.Object.defineProperties(ps.prototype,{m:{configurable:!0,enumerable:!0,get:function(){return!0}}});function qs(){}
r(qs,Sq);qs.prototype.l=function(){return js};
qs.prototype.i=function(a){return a.feedbackEndpoint};
qs.prototype.j=function(a,b,c){a.feedbackTokens=[];b.feedbackToken&&a.feedbackTokens.push(b.feedbackToken);if(b=b.cpn||c.cpn)a.feedbackContext={cpn:b};a.isFeedbackTokenUnencrypted=!!c.is_feedback_token_unencrypted;a.shouldMerge=!1;c.extra_feedback_tokens&&(a.shouldMerge=!0,a.feedbackTokens=a.feedbackTokens.concat(c.extra_feedback_tokens))};
da.Object.defineProperties(qs.prototype,{m:{configurable:!0,enumerable:!0,get:function(){return!0}}});function rs(){}
r(rs,Sq);rs.prototype.l=function(){return ks};
rs.prototype.i=function(a){return a.modifyChannelNotificationPreferenceEndpoint};
rs.prototype.j=function(a,b){b.params&&(a.params=b.params);b.secondaryParams&&(a.secondaryParams=b.secondaryParams)};function ss(){}
r(ss,Sq);ss.prototype.l=function(){return ls};
ss.prototype.i=function(a){return a.playlistEditEndpoint};
ss.prototype.j=function(a,b){b.actions&&(a.actions=b.actions);b.params&&(a.params=b.params);b.playlistId&&(a.playlistId=b.playlistId)};function ts(){}
r(ts,Sq);ts.prototype.l=function(){return is};
ts.prototype.i=function(a){return a.webPlayerShareEntityServiceEndpoint};
ts.prototype.j=function(a,b,c){c=void 0===c?{}:c;b.serializedShareEntity&&(a.serializedSharedEntity=b.serializedShareEntity);c.includeListId&&(a.includeListId=!0)};var zq=new xq("NETWORK_SLI_TOKEN");function us(a){this.i=a}
us.prototype.fetch=function(a,b){var c=this,d,e;return w(function(f){c.i&&(d=ac(bc(5,nc(a)))||"/UNKNOWN_PATH",c.i.start(d));e=new window.Request(a,b);return M("web_fetch_promise_cleanup_killswitch")?f.return(Promise.resolve(fetch(e).then(function(g){return c.handleResponse(g)}).catch(function(g){Ep(g)}))):f.return(fetch(e).then(function(g){return c.handleResponse(g)}).catch(function(g){Ep(g)}))})};
us.prototype.handleResponse=function(a){var b=a.text().then(function(c){return JSON.parse(c.replace(")]}'",""))});
a.redirected||a.ok?this.i&&this.i.success():(this.i&&this.i.failure(),b=b.then(function(c){Ep(new Q("Error: API fetch failed",a.status,a.url,c));return Object.assign({},c,{errorMetadata:{status:a.status}})}));
return b};
us[wq]=[new yq];var vs=new xq("NETWORK_MANAGER_TOKEN");var ws;function xs(a){Gl.call(this,1,arguments);this.csn=a}
r(xs,Gl);var Pl=new Hl("screen-created",xs),ys=[],As=zs,Bs=0;function Cs(a,b,c,d,e,f,g){function h(){Ep(new Q("newScreen() parent element does not have a VE - rootVe",b))}
var k=As();f=new Pp({veType:b,youtubeData:f,jspbYoutubeData:void 0});e={cttAuthInfo:e,V:k};if(M("il_via_jspb")){var m=new eh;m.P(k);fh(m,f.getAsJspb());c&&c.visualElement?(f=new gh,c.clientScreenNonce&&C(f,2,c.clientScreenNonce),hh(f,c.visualElement.getAsJspb()),g&&C(f,4,Bh[g]),D(m,5,f)):c&&h();d&&C(m,3,d);up(m,e,a)}else f={csn:k,pageVe:f.getAsJson()},c&&c.visualElement?(f.implicitGesture={parentCsn:c.clientScreenNonce,gesturedVe:c.visualElement.getAsJson()},g&&(f.implicitGesture.gestureType=g)):
c&&h(),d&&(f.cloneCsn=d),a?$o("screenCreated",f,a,e):Yj("screenCreated",f,e);Ml(Pl,new xs(k));return k}
function Ds(a,b,c,d){var e=d.filter(function(k){k.csn!==b?(k.csn=b,k=!0):k=!1;return k}),f={cttAuthInfo:Yp(b),
V:b};d=q(d);for(var g=d.next();!g.done;g=d.next())g=g.value.getAsJson(),(ob(g)||!g.trackingParams&&!g.veType)&&Ep(Error("Child VE logged with no data"));if(M("il_via_jspb")){var h=new ih;h.P(b);kh(h,c.getAsJspb());gb(e,function(k){k=k.getAsJspb();qd(h,3,ch,k)});
"UNDEFINED_CSN"==b?Es("visualElementAttached",h,f):vp(h,f,a)}else c={csn:b,parentVe:c.getAsJson(),childVes:gb(e,function(k){return k.getAsJson()})},"UNDEFINED_CSN"==b?Es("visualElementAttached",c,f):a?$o("visualElementAttached",c,a,f):Yj("visualElementAttached",c,f)}
function zs(){for(var a=Math.random()+"",b=[],c=0,d=0;d<a.length;d++){var e=a.charCodeAt(d);255<e&&(b[c++]=e&255,e>>=8);b[c++]=e}return Lc(b,3)}
function Es(a,b,c){ys.push({payloadName:a,payload:b,options:c});Bs||(Bs=Ql())}
function Rl(a){if(ys){for(var b=q(ys),c=b.next();!c.done;c=b.next()){var d=c.value;if(d.payload)if(M("il_via_jspb"))switch(d.payload.P(a.csn),d.payloadName){case "screenCreated":up(d.payload,d.options);break;case "visualElementAttached":vp(d.payload,d.options);break;case "visualElementShown":c=d.payload;d=d.options;var e=new zh;pd(e,72,Ah,c);qp(e,d);break;case "visualElementHidden":rp(d.payload,d.options);break;case "visualElementGestured":sp(d.payload,d.options);break;case "visualElementStateChanged":tp(d.payload,
d.options);break;default:Ep(new Q("flushQueue unable to map payloadName to JSPB setter"))}else d.payload.csn=a.csn,$o(d.payloadName,d.payload,null,d.options)}ys.length=0}Bs=0}
;function Fs(){this.j=new Set;this.i=new Set;this.l=new Map}
Fs.prototype.clear=function(){this.j.clear();this.i.clear();this.l.clear()};
Ma(Fs);function Gs(){this.s=[];this.D=[];this.i=[];this.m=[];this.o=[];this.j=new Set;this.v=new Map}
function Hs(a,b,c){c=void 0===c?0:c;b.then(function(d){a.j.has(c)&&a.l&&a.l();var e=Wp(c),f=Up(c);if(e&&f){var g;(null==d?0:null==(g=d.response)?0:g.trackingParams)&&Ds(a.client,e,f,[Qp(d.response.trackingParams)]);var h;(null==d?0:null==(h=d.playerResponse)?0:h.trackingParams)&&Ds(a.client,e,f,[Qp(d.playerResponse.trackingParams)])}})}
function Is(a,b,c,d){d=void 0===d?0:d;if(a.j.has(d))a.s.push([b,c]);else{var e=Wp(d);c=c||Up(d);e&&c&&Ds(a.client,e,c,[b])}}
Gs.prototype.clickCommand=function(a,b,c){var d=a.clickTrackingParams;c=void 0===c?0:c;if(d)if(c=Wp(void 0===c?0:c)){a=this.client;var e=Qp(d);var f="INTERACTION_LOGGING_GESTURE_TYPE_GENERIC_CLICK";d={cttAuthInfo:Yp(c),V:c};if(M("il_via_jspb")){var g=new lh;g.P(c);e=e.getAsJspb();D(g,2,e);C(g,4,Bh[f]);b&&D(g,3);"UNDEFINED_CSN"==c?Es("visualElementGestured",g,d):sp(g,d,a)}else f={csn:c,ve:e.getAsJson(),gestureType:f},b&&(f.clientData=b),"UNDEFINED_CSN"==c?Es("visualElementGestured",f,d):a?$o("visualElementGestured",
f,a,d):Yj("visualElementGestured",f,d);b=!0}else b=!1;else b=!1;return b};
function Js(a,b,c){c=void 0===c?{}:c;a.j.add(c.layer||0);a.l=function(){Ks(a,b,c);var f=Up(c.layer);if(f){for(var g=q(a.s),h=g.next();!h.done;h=g.next())h=h.value,Is(a,h[0],h[1]||f,c.layer);f=q(a.D);for(g=f.next();!g.done;g=f.next()){var k=g.value;g=void 0;g=void 0===g?0:g;h=Wp(g);var m=k[0]||Up(g);if(h&&m){g=a.client;var p=k[1];k={cttAuthInfo:Yp(h),V:h};M("il_via_jspb")?(p=new nh,p.P(h),m=m.getAsJspb(),D(p,2,m),"UNDEFINED_CSN"==h?Es("visualElementStateChanged",p,k):tp(p,k,g)):(m={csn:h,ve:m.getAsJson(),
clientData:p},"UNDEFINED_CSN"==h?Es("visualElementStateChanged",m,k):g?$o("visualElementStateChanged",m,g,k):Yj("visualElementStateChanged",m,k))}}}};
Wp(c.layer)||a.l();if(c.pb)for(var d=q(c.pb),e=d.next();!e.done;e=d.next())Hs(a,e.value,c.layer);else Dp(Error("Delayed screen needs a data promise."))}
function Ks(a,b,c){c=void 0===c?{}:c;c.layer||(c.layer=0);var d=void 0!==c.cc?c.cc:c.layer;var e=Wp(d);d=Up(d);var f;d&&(void 0!==c.parentCsn?f={clientScreenNonce:c.parentCsn,visualElement:d}:e&&"UNDEFINED_CSN"!==e&&(f={clientScreenNonce:e,visualElement:d}));var g,h=L("EVENT_ID");"UNDEFINED_CSN"===e&&h&&(g={servletData:{serializedServletEventId:h}});try{var k=Cs(a.client,b,f,c.ob,c.cttAuthInfo,g,c.kp)}catch(m){Gp(m,{up:b,rootVe:d,parentVisualElement:void 0,ip:e,qp:f,ob:c.ob});Dp(m);return}Zp(k,b,
c.layer,c.cttAuthInfo);if(b=e&&"UNDEFINED_CSN"!==e&&d){a:{b=q(Object.values(Op));for(f=b.next();!f.done;f=b.next())if(Wp(f.value)===e){b=!0;break a}b=!1}b=!b}b&&(b=a.client,g=!0,h=(g=void 0===g?!1:g)?16:8,f={cttAuthInfo:Yp(e),V:e,rb:g},M("il_via_jspb")?(h=new mh,h.P(e),d=d.getAsJspb(),D(h,2,d),C(h,4,g?16:8),"UNDEFINED_CSN"==e?Es("visualElementHidden",h,f):rp(h,f,b)):(d={csn:e,ve:d.getAsJson(),eventType:h},"UNDEFINED_CSN"==e?Es("visualElementHidden",d,f):b?$o("visualElementHidden",d,b,f):Yj("visualElementHidden",
d,f)));a.i[a.i.length-1]&&!a.i[a.i.length-1].csn&&(a.i[a.i.length-1].csn=k||"");Sr({clientScreenNonce:k});Fs.getInstance().clear();d=Up(c.layer);e&&"UNDEFINED_CSN"!==e&&d&&(M("web_mark_root_visible")||M("music_web_mark_root_visible"))&&(e={cttAuthInfo:Yp(k),V:k},M("il_via_jspb")?(b=new mh,b.P(k),f=d.getAsJspb(),D(b,2,f),C(b,4,1),"UNDEFINED_CSN"==k?Es("visualElementShown",b,e):(k=new zh,pd(k,72,Ah,b),qp(k,e))):(b={csn:k,ve:d.getAsJson(),eventType:1},"UNDEFINED_CSN"==k?Es("visualElementShown",b,e):
Yj("visualElementShown",b,e)));a.j.delete(c.layer||0);a.l=void 0;e=q(a.v);for(k=e.next();!k.done;k=e.next())b=q(k.value),k=b.next().value,b=b.next().value,b.has(c.layer)&&d&&Is(a,k,d,c.layer);for(c=0;c<a.m.length;c++){e=a.m[c];try{e()}catch(m){Dp(m)}}for(c=a.m.length=0;c<a.o.length;c++){e=a.o[c];try{e()}catch(m){Dp(m)}}}
;function Ls(){var a,b;return w(function(c){if(1==c.i)return a=cs.i,a?t(c,es(a),2):(Ep(Error("InnertubeTransportService unavailable in fetchDatasyncIds")),c.return(void 0));if(b=c.j)return b.errorMetadata?(Ep(Error("Datasync IDs fetch responded with "+b.errorMetadata.status+": "+b.error)),c.return(void 0)):c.return(b.jp);Ep(Error("Network request to get Datasync IDs failed."));return c.return(void 0)})}
;var Ms=y.caches,Ns;function Os(a){var b=a.indexOf(":");return-1===b?{Ab:a}:{Ab:a.substring(0,b),datasyncId:a.substring(b+1)}}
function Ps(){return w(function(a){if(void 0!==Ns)return a.return(Ns);Ns=new Promise(function(b){var c;return w(function(d){switch(d.i){case 1:return wa(d,2),t(d,Ms.open("test-only"),4);case 4:return t(d,Ms.delete("test-only"),5);case 5:xa(d,3);break;case 2:if(c=ya(d),c instanceof Error&&"SecurityError"===c.name)return b(!1),d.return();case 3:b("caches"in window),d.i=0}})});
return a.return(Ns)})}
function Qs(a){var b,c,d,e,f,g,h;w(function(k){if(1==k.i)return t(k,Ps(),2);if(3!=k.i){if(!k.j)return k.return(!1);b=[];return t(k,Ms.keys(),3)}c=k.j;d=q(c);for(e=d.next();!e.done;e=d.next())f=e.value,g=Os(f),h=g.datasyncId,!h||a.includes(h)||b.push(Ms.delete(f));return k.return(Promise.all(b).then(function(m){return m.some(function(p){return p})}))})}
function Rs(){var a,b,c,d,e,f,g;return w(function(h){if(1==h.i)return t(h,Ps(),2);if(3!=h.i){if(!h.j)return h.return(!1);a=ck("cache contains other");return t(h,Ms.keys(),3)}b=h.j;c=q(b);for(d=c.next();!d.done;d=c.next())if(e=d.value,f=Os(e),(g=f.datasyncId)&&g!==a)return h.return(!0);return h.return(!1)})}
;function Ss(){try{return!!self.localStorage}catch(a){return!1}}
;function Ts(a){a=a.match(/(.*)::.*::.*/);if(null!==a)return a[1]}
function Us(a){if(Ss()){var b=Object.keys(window.localStorage);b=q(b);for(var c=b.next();!c.done;c=b.next()){c=c.value;var d=Ts(c);void 0===d||a.includes(d)||self.localStorage.removeItem(c)}}}
function Vs(){if(!Ss())return!1;var a=ck(),b=Object.keys(window.localStorage);b=q(b);for(var c=b.next();!c.done;c=b.next())if(c=Ts(c.value),void 0!==c&&c!==a)return!0;return!1}
;function Ws(){Ls().then(function(a){a&&(nl(a),Qs(a),Us(a))})}
function Xs(){var a=new Bm;af.M(function(){var b,c,d,e;return w(function(f){switch(f.i){case 1:if(M("ytidb_clear_optimizations_killswitch")){f.u(2);break}b=ck("clear");if(b.startsWith("V")){var g=[b];nl(g);Qs(g);Us(g);return f.return()}c=Vs();return t(f,Rs(),3);case 3:return d=f.j,t(f,ol(),4);case 4:if(e=f.j,!c&&!d&&!e)return f.return();case 2:a.H()?Ws():a.m.add("publicytnetworkstatus-online",Ws,!0,void 0,void 0),f.i=0}})})}
;function Ys(a){a&&(a.dataset?a.dataset[Zs("loaded")]="true":a.setAttribute("data-loaded","true"))}
function $s(a,b){return a?a.dataset?a.dataset[Zs(b)]:a.getAttribute("data-"+b):null}
var at={};function Zs(a){return at[a]||(at[a]=String(a).replace(/\-([a-z])/g,function(b,c){return c.toUpperCase()}))}
;var bt=/\.vflset|-vfl[a-zA-Z0-9_+=-]+/,ct=/-[a-zA-Z]{2,3}_[a-zA-Z]{2,3}(?=(\/|$))/;function dt(a,b,c){c=void 0===c?null:c;if(window.spf&&spf.script){c="";if(a){var d=a.indexOf("jsbin/"),e=a.lastIndexOf(".js"),f=d+6;-1<d&&-1<e&&e>f&&(c=a.substring(f,e),c=c.replace(bt,""),c=c.replace(ct,""),c=c.replace("debug-",""),c=c.replace("tracing-",""))}spf.script.load(a,c,b)}else et(a,b,c)}
function et(a,b,c){c=void 0===c?null:c;var d=ft(a),e=document.getElementById(d),f=e&&$s(e,"loaded"),g=e&&!f;f?b&&b():(b&&(f=Cn(d,b),b=""+Ra(b),gt[b]=f),g||(e=ht(a,d,function(){$s(e,"loaded")||(Ys(e),Fn(d),oi(Xa(Gn,d),0))},c)))}
function ht(a,b,c,d){d=void 0===d?null:d;var e=Ld("SCRIPT");e.id=b;e.onload=function(){c&&setTimeout(c,0)};
e.onreadystatechange=function(){switch(e.readyState){case "loaded":case "complete":e.onload()}};
d&&e.setAttribute("nonce",d);Hd(e,Ib(a));a=document.getElementsByTagName("head")[0]||document.body;a.insertBefore(e,a.firstChild);return e}
function jt(a){a=ft(a);var b=document.getElementById(a);b&&(Gn(a),b.parentNode.removeChild(b))}
function kt(a,b){a&&b&&(a=""+Ra(b),(a=gt[a])&&En(a))}
function ft(a){var b=document.createElement("a");Ub(b,a);a=b.href.replace(/^[a-zA-Z]+:\/\//,"//");return"js-"+Zb(a)}
var gt={};var lt=[],mt=!1;function nt(){if(!M("disable_biscotti_fetch_for_ad_blocker_detection")&&!M("disable_biscotti_fetch_entirely_for_all_web_clients")&&kq()){var a=L("PLAYER_VARS",{});if(!("1"==qb(a)||M("embeds_web_disable_ads_for_pfl")&&"EMBEDDED_PLAYER_MODE_PFL"===lq(a))){var b=function(){mt=!0;"google_ad_status"in window?Mh("DCLKSTAT",1):Mh("DCLKSTAT",2)};
try{dt("//static.doubleclick.net/instream/ad_status.js",b)}catch(c){}lt.push(af.M(function(){if(!(mt||"google_ad_status"in window)){try{kt("//static.doubleclick.net/instream/ad_status.js",b)}catch(c){}mt=!0;Mh("DCLKSTAT",3)}},5E3))}}}
function qt(){var a=Number(L("DCLKSTAT",0));return isNaN(a)?0:a}
;function rt(){this.state=1;this.i=null}
rt.prototype.initialize=function(a,b,c){if(a.program){var d,e=null!=(d=a.interpreterScript)?d:null,f;d=null!=(f=a.interpreterUrl)?f:null;a.interpreterSafeScript&&(e=a.interpreterSafeScript,Cb("From proto message. b/166824318"),e=e.privateDoNotAccessOrElseSafeScriptWrappedValue||"",e=(f=zb())?f.createScript(e):e,e=(new Eb(e)).toString());a.interpreterSafeUrl&&(d=a.interpreterSafeUrl,Cb("From proto message. b/166824318"),d=Ib(d.privateDoNotAccessOrElseTrustedResourceUrlWrappedValue||"").toString());
st(this,e,d,a.program,b,c)}else Ep(Error("Cannot initialize botguard without program"))};
function st(a,b,c,d,e,f){var g=void 0===g?"trayride":g;c?(a.state=2,dt(c,function(){window[g]?tt(a,d,g,e):(a.state=3,jt(c),Ep(new Q("Unable to load Botguard","from "+c)))},f)):b?(f=Ld("SCRIPT"),f.textContent=b,f.nonce=Xb(),document.head.appendChild(f),document.head.removeChild(f),window[g]?tt(a,d,g,e):(a.state=4,Ep(new Q("Unable to load Botguard from JS")))):Ep(new Q("Unable to load VM; no url or JS provided"))}
rt.prototype.isInitialized=function(){return!!this.i};
function tt(a,b,c,d){a.state=5;try{var e=new Ad({program:b,globalName:c});e.uc.then(function(){a.state=6;d&&d(b)});
ut(a,e)}catch(f){a.state=7,f instanceof Error&&Ep(f)}}
rt.prototype.invoke=function(a){a=void 0===a?{}:a;if(this.i){var b=this.i;a={nb:a};if(b.j)throw Error("Already disposed");b=b.m([a.nb,a.wc])}else b=null;return b};
rt.prototype.dispose=function(){ut(this,null);this.state=8};
function ut(a,b){oe(a.i);a.i=b}
;var vt=new rt;function wt(){return vt.isInitialized()}
function xt(a){a=void 0===a?{}:a;return vt.invoke(a)}
;function zt(a){var b=this;var c=void 0===c?0:c;var d=void 0===d?Nj():d;this.m=c;this.l=d;this.j=new zd;this.i=a;a={};c=q(this.i.entries());for(d=c.next();!d.done;a={qa:a.qa,za:a.za},d=c.next()){var e=q(d.value);d=e.next().value;e=e.next().value;a.za=d;a.qa=e;d=function(f){return function(){f.qa.bb();b.i[f.za].Qa=!0;b.i.every(function(g){return!0===g.Qa})&&b.j.resolve()}}(a);
e=Jj(d,At(this,a.qa));this.i[a.za]=Object.assign({},a.qa,{bb:d,Ma:e})}}
function Bt(a){var b=Array.from(a.i.keys()).sort(function(d,e){return At(a,a.i[e])-At(a,a.i[d])});
b=q(b);for(var c=b.next();!c.done;c=b.next())c=a.i[c.value],void 0===c.Ma||c.Qa||(a.l.S(c.Ma),Jj(c.bb,10))}
zt.prototype.cancel=function(){for(var a=q(this.i),b=a.next();!b.done;b=a.next())b=b.value,void 0===b.Ma||b.Qa||this.l.S(b.Ma),b.Qa=!0;this.j.resolve()};
function At(a,b){var c;return null!=(c=b.priority)?c:a.m}
;function Ct(a){this.state=a;this.plugins=[];this.o=void 0}
Ct.prototype.install=function(){this.plugins.push.apply(this.plugins,fa(Ja.apply(0,arguments)))};
Ct.prototype.transition=function(a,b){var c=this,d=this.D.find(function(f){return f.from===c.state&&f.B===a});
if(d){this.l&&(Bt(this.l),this.l=void 0);this.state=a;d=d.action.bind(this);var e=this.plugins.filter(function(f){return f[a]}).map(function(f){return f[a]});
d(Dt(this,e,this.o),b)}else throw Error("no transition specified from "+this.state+" to "+a);};
function Dt(a,b,c){return function(){var d=Ja.apply(0,arguments),e=b.filter(function(k){var m;return 10===(null!=(m=null!=c?c:k.priority)?m:0)}),f=b.filter(function(k){var m;
return 10!==(null!=(m=null!=c?c:k.priority)?m:0)});
Nj();var g={};e=q(e);for(var h=e.next();!h.done;g={Aa:g.Aa},h=e.next())g.Aa=h.value,Lj(function(k){return function(){k.Aa.ia.apply(k.Aa,fa(d))}}(g));
f=f.map(function(k){var m;return{bb:function(){k.ia.apply(k,fa(d))},
priority:null!=(m=null!=c?c:k.priority)?m:0}});
f.length&&(a.l=new zt(f))}}
da.Object.defineProperties(Ct.prototype,{currentState:{configurable:!0,enumerable:!0,get:function(){return this.state}}});function Et(a){Ct.call(this,void 0===a?"document_active":a);var b=this;this.o=10;this.i=new Map;this.D=[{from:"document_active",B:"document_disposed_preventable",action:this.v},{from:"document_active",B:"document_disposed",action:this.m},{from:"document_disposed_preventable",B:"document_disposed",action:this.m},{from:"document_disposed_preventable",B:"flush_logs",action:this.s},{from:"document_disposed_preventable",B:"document_active",action:this.j},{from:"document_disposed",B:"flush_logs",action:this.s},
{from:"document_disposed",B:"document_active",action:this.j},{from:"document_disposed",B:"document_disposed",action:function(){}},
{from:"flush_logs",B:"document_active",action:this.j}];window.addEventListener("pagehide",function(c){b.transition("document_disposed",{event:c})});
window.addEventListener("beforeunload",function(c){b.transition("document_disposed_preventable",{event:c})})}
r(Et,Ct);Et.prototype.v=function(a,b){if(!this.i.get("document_disposed_preventable")){a(null==b?void 0:b.event);var c,d;if((null==b?0:null==(c=b.event)?0:c.defaultPrevented)||(null==b?0:null==(d=b.event)?0:d.returnValue)){b.event.returnValue||(b.event.returnValue=!0);b.event.defaultPrevented||b.event.preventDefault();this.i=new Map;this.transition("document_active");return}}this.i.set("document_disposed_preventable",!0);this.i.get("document_disposed")?this.transition("flush_logs"):this.transition("document_disposed")};
Et.prototype.m=function(a,b){this.i.get("document_disposed")?this.transition("document_active"):(a(null==b?void 0:b.event),this.i.set("document_disposed",!0),this.transition("flush_logs"))};
Et.prototype.s=function(a,b){a(null==b?void 0:b.event);this.transition("document_active")};
Et.prototype.j=function(){this.i=new Map};function Ft(a){Ct.call(this,void 0===a?"document_visibility_unknown":a);var b=this;this.D=[{from:"document_visibility_unknown",B:"document_visible",action:this.j},{from:"document_visibility_unknown",B:"document_hidden",action:this.i},{from:"document_visibility_unknown",B:"document_foregrounded",action:this.s},{from:"document_visibility_unknown",B:"document_backgrounded",action:this.m},{from:"document_visible",B:"document_hidden",action:this.i},{from:"document_visible",B:"document_foregrounded",action:this.s},
{from:"document_visible",B:"document_visible",action:this.j},{from:"document_foregrounded",B:"document_visible",action:this.j},{from:"document_foregrounded",B:"document_hidden",action:this.i},{from:"document_foregrounded",B:"document_foregrounded",action:this.s},{from:"document_hidden",B:"document_visible",action:this.j},{from:"document_hidden",B:"document_backgrounded",action:this.m},{from:"document_hidden",B:"document_hidden",action:this.i},{from:"document_backgrounded",B:"document_hidden",action:this.i},
{from:"document_backgrounded",B:"document_backgrounded",action:this.m},{from:"document_backgrounded",B:"document_visible",action:this.j}];document.addEventListener("visibilitychange",function(c){"visible"===document.visibilityState?b.transition("document_visible",{event:c}):b.transition("document_hidden",{event:c})});
M("visibility_lifecycles_dynamic_backgrounding")&&(window.addEventListener("blur",function(c){b.transition("document_backgrounded",{event:c})}),window.addEventListener("focus",function(c){b.transition("document_foregrounded",{event:c})}))}
r(Ft,Ct);Ft.prototype.j=function(a,b){a(null==b?void 0:b.event);M("visibility_lifecycles_dynamic_backgrounding")&&this.transition("document_foregrounded")};
Ft.prototype.i=function(a,b){a(null==b?void 0:b.event);M("visibility_lifecycles_dynamic_backgrounding")&&this.transition("document_backgrounded")};
Ft.prototype.m=function(a,b){a(null==b?void 0:b.event)};
Ft.prototype.s=function(a,b){a(null==b?void 0:b.event)};function Gt(){this.i=new Et;this.j=new Ft}
Gt.prototype.install=function(){var a=Ja.apply(0,arguments);this.i.install.apply(this.i,fa(a));this.j.install.apply(this.j,fa(a))};function Ht(){Gt.call(this);var a={};this.install((a.document_disposed={ia:this.l},a));a={};this.install((a.flush_logs={ia:this.m},a))}
var It;r(Ht,Gt);Ht.prototype.m=function(){if(M("web_fp_via_jspb")){var a=new bh,b=Wp();b&&a.P(b);b=new zh;pd(b,380,Ah,a);qp(b);M("web_fp_via_jspb_and_json")&&Yj("finalPayload",{csn:Wp()})}else Yj("finalPayload",{csn:Wp()})};
Ht.prototype.l=function(){Ip(Jp)};function Jt(){}
Jt.getInstance=function(){var a=A("ytglobal.storage_");a||(a=new Jt,z("ytglobal.storage_",a));return a};
Jt.prototype.estimate=function(){var a,b,c;return w(function(d){a=navigator;return(null==(b=a.storage)?0:b.estimate)?d.return(a.storage.estimate()):(null==(c=a.webkitTemporaryStorage)?0:c.queryUsageAndQuota)?d.return(Kt()):d.return()})};
function Kt(){var a=navigator;return new Promise(function(b,c){var d;null!=(d=a.webkitTemporaryStorage)&&d.queryUsageAndQuota?a.webkitTemporaryStorage.queryUsageAndQuota(function(e,f){b({usage:e,quota:f})},function(e){c(e)}):c(Error("webkitTemporaryStorage is not supported."))})}
z("ytglobal.storageClass_",Jt);function Wj(a,b){var c=this;this.handleError=a;this.i=b;this.j=!1;void 0===self.document||self.addEventListener("beforeunload",function(){c.j=!0});
this.l=Math.random()<=Ph("ytidb_transaction_ended_event_rate_limit",.02)}
Wj.prototype.logEvent=function(a,b){switch(a){case "IDB_DATA_CORRUPTED":M("idb_data_corrupted_killswitch")||this.i("idbDataCorrupted",b);break;case "IDB_UNEXPECTEDLY_CLOSED":this.i("idbUnexpectedlyClosed",b);break;case "IS_SUPPORTED_COMPLETED":M("idb_is_supported_completed_killswitch")||this.i("idbIsSupportedCompleted",b);break;case "QUOTA_EXCEEDED":Lt(this,b);break;case "TRANSACTION_ENDED":this.l&&this.i("idbTransactionEnded",b);break;case "TRANSACTION_UNEXPECTEDLY_ABORTED":a=Object.assign({},b,
{hasWindowUnloaded:this.j}),this.i("idbTransactionAborted",a)}};
function Lt(a,b){Jt.getInstance().estimate().then(function(c){c=Object.assign({},b,{isSw:void 0===self.document,isIframe:self!==self.top,deviceStorageUsageMbytes:Mt(null==c?void 0:c.usage),deviceStorageQuotaMbytes:Mt(null==c?void 0:c.quota)});a.i("idbQuotaExceeded",c)})}
function Mt(a){return"undefined"===typeof a?"-1":String(Math.ceil(a/1048576))}
;function Nt(a,b,c){J.call(this);var d=this;c=c||L("POST_MESSAGE_ORIGIN")||window.document.location.protocol+"//"+window.document.location.hostname;this.l=b||null;this.targetOrigin="*";this.m=c;this.sessionId=null;this.channel="widget";this.L=!!a;this.A=function(e){a:if(!("*"!=d.m&&e.origin!=d.m||d.l&&e.source!=d.l||"string"!==typeof e.data)){try{var f=JSON.parse(e.data)}catch(g){break a}if(!(null==f||d.L&&(d.sessionId&&d.sessionId!=f.id||d.channel&&d.channel!=f.channel))&&f)switch(f.event){case "listening":"null"!=
e.origin&&(d.m=d.targetOrigin=e.origin);d.l=e.source;d.sessionId=f.id;d.j&&(d.j(),d.j=null);break;case "command":d.o&&(!d.v||0<=db(d.v,f.func))&&d.o(f.func,f.args,e.origin)}}};
this.v=this.j=this.o=null;window.addEventListener("message",this.A)}
r(Nt,J);Nt.prototype.sendMessage=function(a,b){if(b=b||this.l){this.sessionId&&(a.id=this.sessionId);this.channel&&(a.channel=this.channel);try{var c=JSON.stringify(a);b.postMessage(c,this.targetOrigin)}catch(d){Wh(d)}}};
Nt.prototype.I=function(){window.removeEventListener("message",this.A);J.prototype.I.call(this)};function Ot(){this.j=[];this.isReady=!1;this.l={};var a=this.i=new Nt(!!L("WIDGET_ID_ENFORCE")),b=this.fc.bind(this);a.o=b;a.v=null;this.i.channel="widget";if(a=L("WIDGET_ID"))this.i.sessionId=a}
l=Ot.prototype;l.fc=function(a,b,c){"addEventListener"===a&&b?(a=b[0],this.l[a]||"onReady"===a||(this.addEventListener(a,Pt(this,a)),this.l[a]=!0)):this.ib(a,b,c)};
l.ib=function(){};
function Pt(a,b){return function(c){return a.sendMessage(b,c)}}
l.addEventListener=function(){};
l.Tb=function(){this.isReady=!0;this.sendMessage("initialDelivery",this.Wa());this.sendMessage("onReady");eb(this.j,this.Gb,this);this.j=[]};
l.Wa=function(){return null};
function Qt(a,b){a.sendMessage("infoDelivery",b)}
l.Gb=function(a){this.isReady?this.i.sendMessage(a):this.j.push(a)};
l.sendMessage=function(a,b){this.Gb({event:a,info:void 0===b?null:b})};
l.dispose=function(){this.i=null};function Rt(a){return(0===a.search("cue")||0===a.search("load"))&&"loadModule"!==a}
function St(a,b,c){if("string"===typeof a)return{videoId:a,startSeconds:b,suggestedQuality:c};b=["endSeconds","startSeconds","mediaContentUrl","suggestedQuality","videoId"];c={};for(var d=0;d<b.length;d++){var e=b[d];a[e]&&(c[e]=a[e])}return c}
function Tt(a,b,c,d){if(Qa(a)&&!Array.isArray(a)){b="playlist list listType index startSeconds suggestedQuality".split(" ");c={};for(d=0;d<b.length;d++){var e=b[d];a[e]&&(c[e]=a[e])}return c}b={index:b,startSeconds:c,suggestedQuality:d};"string"===typeof a&&16===a.length?b.list="PL"+a:b.playlist=a;return b}
;function Ut(a){Ot.call(this);this.listeners=[];this.api=a;this.addEventListener("onReady",this.onReady.bind(this));this.addEventListener("onVideoProgress",this.qc.bind(this));this.addEventListener("onVolumeChange",this.sc.bind(this));this.addEventListener("onApiChange",this.kc.bind(this));this.addEventListener("onPlaybackQualityChange",this.nc.bind(this));this.addEventListener("onPlaybackRateChange",this.oc.bind(this));this.addEventListener("onStateChange",this.pc.bind(this));this.addEventListener("onWebglSettingsChanged",
this.tc.bind(this))}
r(Ut,Ot);l=Ut.prototype;
l.ib=function(a,b,c){if(this.api.isExternalMethodAvailable(a,c)){b=b||[];if(0<b.length&&Rt(a)){var d=b;if(Qa(d[0])&&!Array.isArray(d[0]))var e=d[0];else switch(e={},a){case "loadVideoById":case "cueVideoById":e=St(d[0],void 0!==d[1]?Number(d[1]):void 0,d[2]);break;case "loadVideoByUrl":case "cueVideoByUrl":e=d[0];"string"===typeof e&&(e={mediaContentUrl:e,startSeconds:void 0!==d[1]?Number(d[1]):void 0,suggestedQuality:d[2]});b:{if((d=e.mediaContentUrl)&&(d=/\/([ve]|embed)\/([^#?]+)/.exec(d))&&d[2]){d=
d[2];break b}d=null}e.videoId=d;e=St(e);break;case "loadPlaylist":case "cuePlaylist":e=Tt(d[0],d[1],d[2],d[3])}b.length=1;b[0]=e}this.api.handleExternalCall(a,b,c);Rt(a)&&Qt(this,this.Wa())}};
l.onReady=function(){var a=this.Tb.bind(this);this.i.j=a};
l.addEventListener=function(a,b){this.listeners.push({eventType:a,listener:b});this.api.addEventListener(a,b)};
l.Wa=function(){if(!this.api)return null;var a=this.api.getApiInterface();jb(a,"getVideoData");for(var b={apiInterface:a},c=0,d=a.length;c<d;c++){var e=a[c];if(0===e.search("get")||0===e.search("is")){var f=0;0===e.search("get")?f=3:0===e.search("is")&&(f=2);f=e.charAt(f).toLowerCase()+e.substr(f+1);try{var g=this.api[e]();b[f]=g}catch(h){}}}b.videoData=this.api.getVideoData();b.currentTimeLastUpdated_=Date.now()/1E3;return b};
l.pc=function(a){a={playerState:a,currentTime:this.api.getCurrentTime(),duration:this.api.getDuration(),videoData:this.api.getVideoData(),videoStartBytes:0,videoBytesTotal:this.api.getVideoBytesTotal(),videoLoadedFraction:this.api.getVideoLoadedFraction(),playbackQuality:this.api.getPlaybackQuality(),availableQualityLevels:this.api.getAvailableQualityLevels(),currentTimeLastUpdated_:Date.now()/1E3,playbackRate:this.api.getPlaybackRate(),mediaReferenceTime:this.api.getMediaReferenceTime()};this.api.getVideoUrl&&
(a.videoUrl=this.api.getVideoUrl());this.api.getVideoContentRect&&(a.videoContentRect=this.api.getVideoContentRect());this.api.getProgressState&&(a.progressState=this.api.getProgressState());this.api.getPlaylist&&(a.playlist=this.api.getPlaylist());this.api.getPlaylistIndex&&(a.playlistIndex=this.api.getPlaylistIndex());this.api.getStoryboardFormat&&(a.storyboardFormat=this.api.getStoryboardFormat());Qt(this,a)};
l.nc=function(a){Qt(this,{playbackQuality:a})};
l.oc=function(a){Qt(this,{playbackRate:a})};
l.kc=function(){for(var a=this.api.getOptions(),b={namespaces:a},c=0,d=a.length;c<d;c++){var e=a[c],f=this.api.getOptions(e);b[e]={options:f};for(var g=0,h=f.length;g<h;g++){var k=f[g],m=this.api.getOption(e,k);b[e][k]=m}}this.sendMessage("apiInfoDelivery",b)};
l.sc=function(){Qt(this,{muted:this.api.isMuted(),volume:this.api.getVolume()})};
l.qc=function(a){a={currentTime:a,videoBytesLoaded:this.api.getVideoBytesLoaded(),videoLoadedFraction:this.api.getVideoLoadedFraction(),currentTimeLastUpdated_:Date.now()/1E3,playbackRate:this.api.getPlaybackRate(),mediaReferenceTime:this.api.getMediaReferenceTime()};this.api.getProgressState&&(a.progressState=this.api.getProgressState());Qt(this,a)};
l.tc=function(){var a={sphericalProperties:this.api.getSphericalProperties()};Qt(this,a)};
l.dispose=function(){Ot.prototype.dispose.call(this);for(var a=0;a<this.listeners.length;a++){var b=this.listeners[a];this.api.removeEventListener(b.eventType,b.listener)}this.listeners=[]};function Vt(a){J.call(this);this.j={};this.started=!1;this.connection=a;this.connection.subscribe("command",this.Cb,this)}
r(Vt,J);l=Vt.prototype;l.start=function(){this.started||this.i()||(this.started=!0,this.connection.ja("RECEIVING"))};
l.ja=function(a,b){this.started&&!this.i()&&this.connection.ja(a,b)};
l.Cb=function(a,b,c){if(this.started&&!this.i()){var d=b||{};switch(a){case "addEventListener":"string"===typeof d.event&&this.addListener(d.event);break;case "removeEventListener":"string"===typeof d.event&&this.removeListener(d.event);break;default:this.api.isReady()&&this.api.isExternalMethodAvailable(a,c||null)&&(b=Wt(a,b||{}),c=this.api.handleExternalCall(a,b,c||null),(c=Xt(a,c))&&this.ja(a,c))}}};
l.addListener=function(a){if(!(a in this.j)){var b=this.lc.bind(this,a);this.j[a]=b;this.addEventListener(a,b)}};
l.lc=function(a,b){this.started&&!this.i()&&this.connection.ja(a,this.Va(a,b))};
l.Va=function(a,b){if(null!=b)return{value:b}};
l.removeListener=function(a){a in this.j&&(this.removeEventListener(a,this.j[a]),delete this.j[a])};
l.I=function(){var a=this.connection;a.i()||mg(a.j,"command",this.Cb,this);this.connection=null;for(var b in this.j)this.j.hasOwnProperty(b)&&this.removeListener(b);J.prototype.I.call(this)};function Yt(a,b){Vt.call(this,b);this.api=a;this.start()}
r(Yt,Vt);Yt.prototype.addEventListener=function(a,b){this.api.addEventListener(a,b)};
Yt.prototype.removeEventListener=function(a,b){this.api.removeEventListener(a,b)};
function Wt(a,b){switch(a){case "loadVideoById":return a=St(b),[a];case "cueVideoById":return a=St(b),[a];case "loadVideoByPlayerVars":return[b];case "cueVideoByPlayerVars":return[b];case "loadPlaylist":return a=Tt(b),[a];case "cuePlaylist":return a=Tt(b),[a];case "seekTo":return[b.seconds,b.allowSeekAhead];case "playVideoAt":return[b.index];case "setVolume":return[b.volume];case "setPlaybackQuality":return[b.suggestedQuality];case "setPlaybackRate":return[b.suggestedRate];case "setLoop":return[b.loopPlaylists];
case "setShuffle":return[b.shufflePlaylist];case "getOptions":return[b.module];case "getOption":return[b.module,b.option];case "setOption":return[b.module,b.option,b.value];case "handleGlobalKeyDown":return[b.keyCode,b.shiftKey,b.ctrlKey,b.altKey,b.metaKey,b.key,b.code]}return[]}
function Xt(a,b){switch(a){case "isMuted":return{muted:b};case "getVolume":return{volume:b};case "getPlaybackRate":return{playbackRate:b};case "getAvailablePlaybackRates":return{availablePlaybackRates:b};case "getVideoLoadedFraction":return{videoLoadedFraction:b};case "getPlayerState":return{playerState:b};case "getCurrentTime":return{currentTime:b};case "getPlaybackQuality":return{playbackQuality:b};case "getAvailableQualityLevels":return{availableQualityLevels:b};case "getDuration":return{duration:b};
case "getVideoUrl":return{videoUrl:b};case "getVideoEmbedCode":return{videoEmbedCode:b};case "getPlaylist":return{playlist:b};case "getPlaylistIndex":return{playlistIndex:b};case "getOptions":return{options:b};case "getOption":return{option:b}}}
Yt.prototype.Va=function(a,b){switch(a){case "onReady":return;case "onStateChange":return{playerState:b};case "onPlaybackQualityChange":return{playbackQuality:b};case "onPlaybackRateChange":return{playbackRate:b};case "onError":return{errorCode:b}}return Vt.prototype.Va.call(this,a,b)};
Yt.prototype.I=function(){Vt.prototype.I.call(this);delete this.api};function Zt(a){a=void 0===a?!1:a;J.call(this);this.j=new K(a);qe(this,Xa(oe,this.j))}
Ya(Zt,J);Zt.prototype.subscribe=function(a,b,c){return this.i()?0:this.j.subscribe(a,b,c)};
Zt.prototype.m=function(a,b){this.i()||this.j.ha.apply(this.j,arguments)};function $t(a,b,c){Zt.call(this);this.l=a;this.destination=b;this.id=c}
r($t,Zt);$t.prototype.ja=function(a,b){this.i()||this.l.ja(this.destination,this.id,a,b)};
$t.prototype.I=function(){this.destination=this.l=null;Zt.prototype.I.call(this)};function au(a,b,c){J.call(this);this.destination=a;this.origin=c;this.j=pn(window,"message",this.l.bind(this));this.connection=new $t(this,a,b);qe(this,Xa(oe,this.connection))}
r(au,J);au.prototype.ja=function(a,b,c,d){this.i()||a!==this.destination||(a={id:b,command:c},d&&(a.data=d),this.destination.postMessage(Nf(a),this.origin))};
au.prototype.l=function(a){var b;if(b=!this.i())if(b=a.origin===this.origin)a:{b=this.destination;do{b:{var c=a.source;do{if(c===b){c=!0;break b}if(c===c.parent)break;c=c.parent}while(null!=c);c=!1}if(c){b=!0;break a}b=b.opener}while(null!=b);b=!1}if(b&&(b=a.data,"string"===typeof b)){try{b=JSON.parse(b)}catch(d){return}b.command&&(c=this.connection,c.i()||c.m("command",b.command,b.data,a.origin))}};
au.prototype.I=function(){qn(this.j);this.destination=null;J.prototype.I.call(this)};function bu(a){a=a||{};var b={},c={};this.url=a.url||"";this.args=a.args||ub(b);this.assets=a.assets||{};this.attrs=a.attrs||ub(c);this.fallback=a.fallback||null;this.fallbackMessage=a.fallbackMessage||null;this.html5=!!a.html5;this.disable=a.disable||{};this.loaded=!!a.loaded;this.messages=a.messages||{}}
bu.prototype.clone=function(){var a=new bu,b;for(b in this)if(this.hasOwnProperty(b)){var c=this[b];"object"==Oa(c)?a[b]=ub(c):a[b]=c}return a};var cu=/cssbin\/(?:debug-)?([a-zA-Z0-9_-]+?)(?:-2x|-web|-rtl|-vfl|.css)/;function du(a){a=a||"";if(window.spf){var b=a.match(cu);spf.style.load(a,b?b[1]:"",void 0)}else eu(a)}
function eu(a){var b=fu(a),c=document.getElementById(b),d=c&&$s(c,"loaded");d||c&&!d||(c=gu(a,b,function(){$s(c,"loaded")||(Ys(c),Fn(b),oi(Xa(Gn,b),0))}))}
function gu(a,b,c){var d=document.createElement("link");d.id=b;d.onload=function(){c&&setTimeout(c,0)};
a=Ib(a);Vb(d,a);(document.getElementsByTagName("head")[0]||document.body).appendChild(d);return d}
function fu(a){var b=Ld("A");Cb("This URL is never added to the DOM");Ub(b,new Kb(a,Nb));a=b.href.replace(/^[a-zA-Z]+:\/\//,"//");return"css-"+Zb(a)}
;function hu(){J.call(this);this.j=[]}
r(hu,J);hu.prototype.I=function(){for(;this.j.length;){var a=this.j.pop();a.target.removeEventListener(a.name,a.ia,void 0)}J.prototype.I.call(this)};function iu(){hu.apply(this,arguments)}
r(iu,hu);function ju(a,b,c,d){J.call(this);var e=this;this.N=b;this.webPlayerContextConfig=d;this.Sa=!1;this.api={};this.Ba=this.v=null;this.R=new K;this.j={};this.da=this.Ca=this.elementId=this.Ta=this.config=null;this.W=!1;this.m=this.A=null;this.Da={};this.Kb=["onReady"];this.lastError=null;this.jb=NaN;this.L={};this.Lb=new iu(this);this.la=0;this.l=this.o=a;qe(this,Xa(oe,this.R));ku(this);lu(this);qe(this,Xa(oe,this.Lb));c?this.la=oi(function(){e.loadNewVideoConfig(c)},0):d&&(mu(this),nu(this))}
r(ju,J);l=ju.prototype;l.getId=function(){return this.N};
l.loadNewVideoConfig=function(a){if(!this.i()){this.la&&(pi(this.la),this.la=0);var b=a||{};b instanceof bu||(b=new bu(b));this.config=b;this.setConfig(a);nu(this);this.isReady()&&ou(this)}};
function mu(a){var b;a.webPlayerContextConfig?b=a.webPlayerContextConfig.rootElementId:b=a.config.attrs.id;a.elementId=b||a.elementId;"video-player"===a.elementId&&(a.elementId=a.N,a.webPlayerContextConfig?a.webPlayerContextConfig.rootElementId=a.N:a.config.attrs.id=a.N);var c;(null==(c=a.l)?void 0:c.id)===a.elementId&&(a.elementId+="-player",a.webPlayerContextConfig?a.webPlayerContextConfig.rootElementId=a.elementId:a.config.attrs.id=a.elementId)}
l.setConfig=function(a){this.Ta=a;this.config=pu(a);mu(this);if(!this.Ca){var b;this.Ca=qu(this,(null==(b=this.config.args)?void 0:b.jsapicallback)||"onYouTubePlayerReady")}this.config.args?this.config.args.jsapicallback=null:this.config.args={jsapicallback:null};var c;if(null==(c=this.config)?0:c.attrs)a=this.config.attrs,(b=a.width)&&this.l&&(this.l.style.width=Zd(Number(b)||b)),(a=a.height)&&this.l&&(this.l.style.height=Zd(Number(a)||a))};
function ou(a){if(a.config&&!0!==a.config.loaded)if(a.config.loaded=!0,!a.config.args||"0"!==a.config.args.autoplay&&0!==a.config.args.autoplay&&!1!==a.config.args.autoplay){var b;a.api.loadVideoByPlayerVars(null!=(b=a.config.args)?b:null)}else a.api.cueVideoByPlayerVars(a.config.args)}
function ru(a){var b=!0,c=su(a);c&&a.config&&(a=tu(a),b=$s(c,"version")===a);return b&&!!A("yt.player.Application.create")}
function nu(a){if(!a.i()&&!a.W){var b=ru(a);if(b&&"html5"===(su(a)?"html5":null))a.da="html5",a.isReady()||uu(a);else if(vu(a),a.da="html5",b&&a.m&&a.o)a.o.appendChild(a.m),uu(a);else{a.config&&(a.config.loaded=!0);var c=!1;a.A=function(){c=!0;var d=wu(a,"player_bootstrap_method")?A("yt.player.Application.createAlternate")||A("yt.player.Application.create"):A("yt.player.Application.create");var e=a.config?pu(a.config):void 0;d&&d(a.o,e,a.webPlayerContextConfig);uu(a)};
a.W=!0;b?a.A():(dt(tu(a),a.A),(b=xu(a))&&du(b),yu(a)&&!c&&z("yt.player.Application.create",null))}}}
function su(a){var b=Kd(a.elementId);!b&&a.l&&a.l.querySelector&&(b=a.l.querySelector("#"+a.elementId));return b}
function uu(a){if(!a.i()){var b=su(a),c=!1;b&&b.getApiInterface&&b.getApiInterface()&&(c=!0);if(c){a.W=!1;if(!wu(a,"html5_remove_not_servable_check_killswitch")){var d;if((null==b?0:b.isNotServable)&&a.config&&(null==b?0:b.isNotServable(null==(d=a.config.args)?void 0:d.video_id)))return}zu(a)}else a.jb=oi(function(){uu(a)},50)}}
function zu(a){ku(a);a.Sa=!0;var b=su(a);if(b){a.v=Au(a,b,"addEventListener");a.Ba=Au(a,b,"removeEventListener");var c=b.getApiInterface();c=c.concat(b.getInternalApiInterface());for(var d=a.api,e=0;e<c.length;e++){var f=c[e];d[f]||(d[f]=Au(a,b,f))}}for(var g in a.j)a.j.hasOwnProperty(g)&&a.v&&a.v(g,a.j[g]);ou(a);a.Ca&&a.Ca(a.api);a.R.ha("onReady",a.api)}
function Au(a,b,c){var d=b[c];return function(){var e=Ja.apply(0,arguments);try{return a.lastError=null,d.apply(b,e)}catch(f){"sendAbandonmentPing"!==c&&(f.params=c,a.lastError=f,Ep(f))}}}
function ku(a){a.Sa=!1;if(a.Ba)for(var b in a.j)a.j.hasOwnProperty(b)&&a.Ba(b,a.j[b]);for(var c in a.L)a.L.hasOwnProperty(c)&&pi(Number(c));a.L={};a.v=null;a.Ba=null;b=a.api;for(var d in b)b.hasOwnProperty(d)&&(b[d]=null);b.addEventListener=function(e,f){a.addEventListener(e,f)};
b.removeEventListener=function(e,f){a.removeEventListener(e,f)};
b.destroy=function(){a.dispose()};
b.getLastError=function(){return a.getLastError()};
b.getPlayerType=function(){return a.getPlayerType()};
b.getCurrentVideoConfig=function(){return a.Ta};
b.loadNewVideoConfig=function(e){a.loadNewVideoConfig(e)};
b.isReady=function(){return a.isReady()}}
l.isReady=function(){return this.Sa};
function lu(a){a.addEventListener("WATCH_LATER_VIDEO_ADDED",function(b){Fn("WATCH_LATER_VIDEO_ADDED",b)});
a.addEventListener("WATCH_LATER_VIDEO_REMOVED",function(b){Fn("WATCH_LATER_VIDEO_REMOVED",b)})}
l.addEventListener=function(a,b){var c=this,d=qu(this,b);d&&(0<=db(this.Kb,a)||this.j[a]||(b=Bu(this,a),this.v&&this.v(a,b)),this.R.subscribe(a,d),"onReady"===a&&this.isReady()&&oi(function(){d(c.api)},0))};
l.removeEventListener=function(a,b){this.i()||(b=qu(this,b))&&mg(this.R,a,b)};
function qu(a,b){var c=b;if("string"===typeof b){if(a.Da[b])return a.Da[b];c=function(){var d=Ja.apply(0,arguments),e=A(b);if(e)try{e.apply(y,d)}catch(f){Dp(f)}};
a.Da[b]=c}return c?c:null}
function Bu(a,b){var c="ytPlayer"+b+a.N;a.j[b]=c;y[c]=function(d){var e=oi(function(){if(!a.i()){a.R.ha(b,null!=d?d:void 0);var f=a.L,g=String(e);g in f&&delete f[g]}},0);
pb(a.L,String(e))};
return c}
l.getPlayerType=function(){return this.da||(su(this)?"html5":null)};
l.getLastError=function(){return this.lastError};
function vu(a){a.cancel();ku(a);a.da=null;a.config&&(a.config.loaded=!1);var b=su(a);b&&(ru(a)||!yu(a)?a.m=b:(b&&b.destroy&&b.destroy(),a.m=null));if(a.o)for(a=a.o;b=a.firstChild;)a.removeChild(b)}
l.cancel=function(){this.A&&kt(tu(this),this.A);pi(this.jb);this.W=!1};
l.I=function(){vu(this);if(this.m&&this.config&&this.m.destroy)try{this.m.destroy()}catch(b){Dp(b)}this.Da=null;for(var a in this.j)this.j.hasOwnProperty(a)&&(y[this.j[a]]=null);this.Ta=this.config=this.api=null;delete this.o;delete this.l;J.prototype.I.call(this)};
function yu(a){var b,c;a=null==(b=a.config)?void 0:null==(c=b.args)?void 0:c.fflags;return!!a&&-1!==a.indexOf("player_destroy_old_version=true")}
function tu(a){return a.webPlayerContextConfig?a.webPlayerContextConfig.jsUrl:(a=a.config.assets)?a.js:""}
function xu(a){return a.webPlayerContextConfig?a.webPlayerContextConfig.cssUrl:(a=a.config.assets)?a.css:""}
function wu(a,b){if(a.webPlayerContextConfig)var c=a.webPlayerContextConfig.serializedExperimentFlags;else{var d;if(null==(d=a.config)?0:d.args)c=a.config.args.fflags}return"true"===bi(c||"","&")[b]}
function pu(a){for(var b={},c=q(Object.keys(a)),d=c.next();!d.done;d=c.next()){d=d.value;var e=a[d];b[d]="object"===typeof e?ub(e):e}return b}
;var Cu={},Du="player_uid_"+(1E9*Math.random()>>>0);function Eu(a,b,c){var d="player";c=void 0===c?!0:c;d="string"===typeof d?Kd(d):d;var e=Du+"_"+Ra(d),f=Cu[e];if(f&&c)return Fu(a,b)?f.api.loadVideoByPlayerVars(a.args||null):f.loadNewVideoConfig(a),f.api;f=new ju(d,e,a,b);Cu[e]=f;Fn("player-added",f.api);qe(f,function(){delete Cu[f.getId()]});
return f.api}
function Fu(a,b){return b&&b.serializedExperimentFlags?b.serializedExperimentFlags.includes("web_player_remove_playerproxy=true"):a&&a.args&&a.args.fflags?a.args.fflags.includes("web_player_remove_playerproxy=true"):!1}
;var Gu=null,Hu=null,Iu=null;function Ju(){var a=Gu.getVideoData(1);a=a.title?a.title+" - YouTube":"YouTube";document.title!==a&&(document.title=a)}
;function Ku(a,b,c){a="ST-"+Zb(a).toString(36);b=b?fc(b):"";c=c||5;kq()&&$i(a,b,c)}
;function Lu(a,b,c){b=void 0===b?{}:b;c=void 0===c?!1:c;var d=L("EVENT_ID");d&&(b.ei||(b.ei=d));if(b){d=a;var e=void 0===e?!0:e;var f=L("VALID_SESSION_TEMPDATA_DOMAINS",[]),g=cc(window.location.href);g&&f.push(g);g=cc(d);if(0<=db(f,g)||!g&&0==d.lastIndexOf("/",0))if(M("autoescape_tempdata_url")&&(f=document.createElement("a"),Ub(f,d),d=f.href),d&&(d=dc(d),f=d.indexOf("#"),d=0>f?d:d.slice(0,f)))if(e&&!b.csn&&(b.itct||b.ved)&&(b=Object.assign({csn:Wp()},b)),h){var h=parseInt(h,10);isFinite(h)&&0<h&&
Ku(d,b,h)}else Ku(d,b)}if(c)return!1;if((window.ytspf||{}).enabled)spf.navigate(a);else{var k=void 0===k?{}:k;var m=void 0===m?"":m;var p=void 0===p?window:p;c=p.location;a=gc(a,k)+m;var u=void 0===u?Yd:u;a:{u=void 0===u?Yd:u;for(k=0;k<u.length;++k)if(m=u[k],m instanceof Td&&m.isValid(a)){u=new Ed(a,Cd);break a}u=void 0}c.href=Gd(u||Fd)}return!0}
;z("yt.setConfig",Mh);z("yt.config.set",Mh);z("yt.setMsg",aq);z("yt.msgs.set",aq);z("yt.logging.errors.log",Dp);
z("writeEmbed",function(){var a=L("PLAYER_CONFIG");if(!a){var b=L("PLAYER_VARS");b&&(a={args:b})}tq(!0);"gvn"===a.args.ps&&(document.body.style.backgroundColor="transparent");a.attrs||(a.attrs={width:"100%",height:"100%",id:"video-player"});var c=document.referrer;b=L("POST_MESSAGE_ORIGIN");window!==window.top&&c&&c!==document.URL&&(a.args.loaderUrl=c);M("embeds_js_api_set_1p_cookie")&&(c=gi(window.location.href),c.embedsTokenValue&&(a.args.embedsTokenValue=c.embedsTokenValue));Qr();if((c=L("WEB_PLAYER_CONTEXT_CONFIGS"))&&
"WEB_PLAYER_CONTEXT_CONFIG_ID_EMBEDDED_PLAYER"in c){c=c.WEB_PLAYER_CONTEXT_CONFIG_ID_EMBEDDED_PLAYER;if(!c.serializedForcedExperimentIds){var d=gi(window.location.href);d.forced_experiments&&(c.serializedForcedExperimentIds=d.forced_experiments)}Gu=Eu(a,c,!1)}else Gu=Eu(a);Gu.addEventListener("onVideoDataChange",Ju);a=L("POST_MESSAGE_ID","player");L("ENABLE_JS_API")?Iu=new Ut(Gu):L("ENABLE_POST_API")&&"string"===typeof a&&"string"===typeof b&&(Hu=new au(window.parent,a,b),Iu=new Yt(Gu,Hu.connection));
nt();M("ytidb_create_logger_embed_killswitch")||Vj();a={};It||(It=new Ht);It.install((a.flush_logs={ia:function(){Lo()}},a));
M("networkless_logging_web_embedded")&&(M("embeds_web_enable_new_nwl")?Im():Qm());M("ytidb_clear_embedded_player")&&af.M(function(){var e;if(!ws){Dq||(Dq=new Aq);var f=Dq;var g={ec:vs,Ib:us};f.providers.set(g.ec,g);g={mb:{feedbackEndpoint:Oq(qs),modifyChannelNotificationPreferenceEndpoint:Oq(rs),playlistEditEndpoint:Oq(ss),subscribeEndpoint:Oq(os),unsubscribeEndpoint:Oq(ps),webPlayerShareEntityServiceEndpoint:Oq(ts)}};var h=M("web_enable_client_location_service")?Kq.getInstance():void 0,k={};h&&(k.client_location=
h);if(void 0===m){uq.i||(uq.i=new uq);var m=uq.i}void 0===e&&(e=f.resolve(vs));ds(g,e,m,k);ws=cs.i}Xs()})});
var Mu=Uh(function(){Vr();var a=dj.getInstance(),b=gj(119),c=1<window.devicePixelRatio;if(document.body&&Af(document.body,"exp-invert-logo"))if(c&&!Af(document.body,"inverted-hdpi")){var d=document.body;if(d.classList)d.classList.add("inverted-hdpi");else if(!Af(d,"inverted-hdpi")){var e=yf(d);zf(d,e+(0<e.length?" inverted-hdpi":"inverted-hdpi"))}}else!c&&Af(document.body,"inverted-hdpi")&&Bf();if(b!=c){b="f"+(Math.floor(119/31)+1);d=hj(b)||0;d=c?d|67108864:d&-67108865;0==d?delete cj[b]:(c=d.toString(16),
cj[b]=c.toString());c=!0;M("web_secure_pref_cookie_killswitch")&&(c=!1);b=a.i;d=[];for(var f in cj)d.push(f+"="+encodeURIComponent(String(cj[f])));$i(b,d.join("&"),63072E3,a.j,c)}Gs.i||(Gs.i=new Gs);a=Gs.i;f=16623;var g=void 0===g?{}:g;Object.values(bq).includes(f)||(Ep(new Q("createClientScreen() called with a non-page VE",f)),f=83769);g.isHistoryNavigation||a.i.push({rootVe:f,key:g.key||""});a.s=[];a.D=[];g.pb?Js(a,f,g):Ks(a,f,g)}),Nu=Uh(function(){Gu&&Gu.sendAbandonmentPing&&Gu.sendAbandonmentPing();
L("PL_ATT")&&vt.dispose();for(var a=af,b=0,c=lt.length;b<c;b++)a.S(lt[b]);lt.length=0;jt("//static.doubleclick.net/instream/ad_status.js");mt=!1;Mh("DCLKSTAT",0);pe(Iu,Hu);Gu&&(Gu.removeEventListener("onVideoDataChange",Ju),Gu.destroy())});
window.addEventListener?(window.addEventListener("load",Mu),window.addEventListener("pagehide",Nu)):window.attachEvent&&(window.attachEvent("onload",Mu),window.attachEvent("onunload",Nu));z("yt.abuse.player.botguardInitialized",A("yt.abuse.player.botguardInitialized")||wt);z("yt.abuse.player.invokeBotguard",A("yt.abuse.player.invokeBotguard")||xt);z("yt.abuse.dclkstatus.checkDclkStatus",A("yt.abuse.dclkstatus.checkDclkStatus")||qt);z("yt.player.exports.navigate",A("yt.player.exports.navigate")||Lu);
z("yt.util.activity.init",A("yt.util.activity.init")||un);z("yt.util.activity.getTimeSinceActive",A("yt.util.activity.getTimeSinceActive")||xn);z("yt.util.activity.setTimestamp",A("yt.util.activity.setTimestamp")||vn);}).call(this);
