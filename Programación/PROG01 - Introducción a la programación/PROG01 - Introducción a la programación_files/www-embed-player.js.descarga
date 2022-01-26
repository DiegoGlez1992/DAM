(function(){/*

 Copyright The Closure Library Authors.
 SPDX-License-Identifier: Apache-2.0
*/
'use strict';var l;function aa(a){var b=0;return function(){return b<a.length?{done:!1,value:a[b++]}:{done:!0}}}
var ba="function"==typeof Object.defineProperties?Object.defineProperty:function(a,b,c){if(a==Array.prototype||a==Object.prototype)return a;a[b]=c.value;return a};
function ca(a){a=["object"==typeof globalThis&&globalThis,a,"object"==typeof window&&window,"object"==typeof self&&self,"object"==typeof global&&global];for(var b=0;b<a.length;++b){var c=a[b];if(c&&c.Math==Math)return c}throw Error("Cannot find global object");}
var da=ca(this);function p(a,b){if(b)a:{var c=da;a=a.split(".");for(var d=0;d<a.length-1;d++){var e=a[d];if(!(e in c))break a;c=c[e]}a=a[a.length-1];d=c[a];b=b(d);b!=d&&null!=b&&ba(c,a,{configurable:!0,writable:!0,value:b})}}
p("Symbol",function(a){function b(f){if(this instanceof b)throw new TypeError("Symbol is not a constructor");return new c(d+(f||"")+"_"+e++,f)}
function c(f,g){this.h=f;ba(this,"description",{configurable:!0,writable:!0,value:g})}
if(a)return a;c.prototype.toString=function(){return this.h};
var d="jscomp_symbol_"+(1E9*Math.random()>>>0)+"_",e=0;return b});
p("Symbol.iterator",function(a){if(a)return a;a=Symbol("Symbol.iterator");for(var b="Array Int8Array Uint8Array Uint8ClampedArray Int16Array Uint16Array Int32Array Uint32Array Float32Array Float64Array".split(" "),c=0;c<b.length;c++){var d=da[b[c]];"function"===typeof d&&"function"!=typeof d.prototype[a]&&ba(d.prototype,a,{configurable:!0,writable:!0,value:function(){return ea(aa(this))}})}return a});
function ea(a){a={next:a};a[Symbol.iterator]=function(){return this};
return a}
function q(a){var b="undefined"!=typeof Symbol&&Symbol.iterator&&a[Symbol.iterator];return b?b.call(a):{next:aa(a)}}
function ha(a){if(!(a instanceof Array)){a=q(a);for(var b,c=[];!(b=a.next()).done;)c.push(b.value);a=c}return a}
var ia="function"==typeof Object.create?Object.create:function(a){function b(){}
b.prototype=a;return new b},ja=function(){function a(){function c(){}
new c;Reflect.construct(c,[],function(){});
return new c instanceof c}
if("undefined"!=typeof Reflect&&Reflect.construct){if(a())return Reflect.construct;var b=Reflect.construct;return function(c,d,e){c=b(c,d);e&&Reflect.setPrototypeOf(c,e.prototype);return c}}return function(c,d,e){void 0===e&&(e=c);
e=ia(e.prototype||Object.prototype);return Function.prototype.apply.call(c,e,d)||e}}(),ka;
if("function"==typeof Object.setPrototypeOf)ka=Object.setPrototypeOf;else{var la;a:{var ma={a:!0},na={};try{na.__proto__=ma;la=na.a;break a}catch(a){}la=!1}ka=la?function(a,b){a.__proto__=b;if(a.__proto__!==b)throw new TypeError(a+" is not extensible");return a}:null}var oa=ka;
function r(a,b){a.prototype=ia(b.prototype);a.prototype.constructor=a;if(oa)oa(a,b);else for(var c in b)if("prototype"!=c)if(Object.defineProperties){var d=Object.getOwnPropertyDescriptor(b,c);d&&Object.defineProperty(a,c,d)}else a[c]=b[c];a.Z=b.prototype}
function pa(){this.D=!1;this.l=null;this.i=void 0;this.h=1;this.o=this.m=0;this.A=this.j=null}
function qa(a){if(a.D)throw new TypeError("Generator is already running");a.D=!0}
pa.prototype.u=function(a){this.i=a};
function ra(a,b){a.j={vb:b,zb:!0};a.h=a.m||a.o}
pa.prototype.return=function(a){this.j={return:a};this.h=this.o};
function v(a,b,c){a.h=c;return{value:b}}
pa.prototype.s=function(a){this.h=a};
function sa(a,b,c){a.m=b;void 0!=c&&(a.o=c)}
function ta(a,b){a.h=b;a.m=0}
function ua(a){a.m=0;var b=a.j.vb;a.j=null;return b}
function va(a){a.A=[a.j];a.m=0;a.o=0}
function wa(a){var b=a.A.splice(0)[0];(b=a.j=a.j||b)?b.zb?a.h=a.m||a.o:void 0!=b.s&&a.o<b.s?(a.h=b.s,a.j=null):a.h=a.o:a.h=0}
function xa(a){this.h=new pa;this.i=a}
function ya(a,b){qa(a.h);var c=a.h.l;if(c)return za(a,"return"in c?c["return"]:function(d){return{value:d,done:!0}},b,a.h.return);
a.h.return(b);return Aa(a)}
function za(a,b,c,d){try{var e=b.call(a.h.l,c);if(!(e instanceof Object))throw new TypeError("Iterator result "+e+" is not an object");if(!e.done)return a.h.D=!1,e;var f=e.value}catch(g){return a.h.l=null,ra(a.h,g),Aa(a)}a.h.l=null;d.call(a.h,f);return Aa(a)}
function Aa(a){for(;a.h.h;)try{var b=a.i(a.h);if(b)return a.h.D=!1,{value:b.value,done:!1}}catch(c){a.h.i=void 0,ra(a.h,c)}a.h.D=!1;if(a.h.j){b=a.h.j;a.h.j=null;if(b.zb)throw b.vb;return{value:b.return,done:!0}}return{value:void 0,done:!0}}
function Ba(a){this.next=function(b){qa(a.h);a.h.l?b=za(a,a.h.l.next,b,a.h.u):(a.h.u(b),b=Aa(a));return b};
this.throw=function(b){qa(a.h);a.h.l?b=za(a,a.h.l["throw"],b,a.h.u):(ra(a.h,b),b=Aa(a));return b};
this.return=function(b){return ya(a,b)};
this[Symbol.iterator]=function(){return this}}
function Ca(a){function b(d){return a.next(d)}
function c(d){return a.throw(d)}
return new Promise(function(d,e){function f(g){g.done?d(g.value):Promise.resolve(g.value).then(b,c).then(f,e)}
f(a.next())})}
function w(a){return Ca(new Ba(new xa(a)))}
function Da(){for(var a=Number(this),b=[],c=a;c<arguments.length;c++)b[c-a]=arguments[c];return b}
p("Reflect",function(a){return a?a:{}});
p("Reflect.construct",function(){return ja});
p("Reflect.setPrototypeOf",function(a){return a?a:oa?function(b,c){try{return oa(b,c),!0}catch(d){return!1}}:null});
p("Promise",function(a){function b(g){this.h=0;this.j=void 0;this.i=[];this.D=!1;var h=this.l();try{g(h.resolve,h.reject)}catch(k){h.reject(k)}}
function c(){this.h=null}
function d(g){return g instanceof b?g:new b(function(h){h(g)})}
if(a)return a;c.prototype.i=function(g){if(null==this.h){this.h=[];var h=this;this.j(function(){h.o()})}this.h.push(g)};
var e=da.setTimeout;c.prototype.j=function(g){e(g,0)};
c.prototype.o=function(){for(;this.h&&this.h.length;){var g=this.h;this.h=[];for(var h=0;h<g.length;++h){var k=g[h];g[h]=null;try{k()}catch(m){this.l(m)}}}this.h=null};
c.prototype.l=function(g){this.j(function(){throw g;})};
b.prototype.l=function(){function g(m){return function(n){k||(k=!0,m.call(h,n))}}
var h=this,k=!1;return{resolve:g(this.S),reject:g(this.o)}};
b.prototype.S=function(g){if(g===this)this.o(new TypeError("A Promise cannot resolve to itself"));else if(g instanceof b)this.ga(g);else{a:switch(typeof g){case "object":var h=null!=g;break a;case "function":h=!0;break a;default:h=!1}h?this.L(g):this.m(g)}};
b.prototype.L=function(g){var h=void 0;try{h=g.then}catch(k){this.o(k);return}"function"==typeof h?this.qa(h,g):this.m(g)};
b.prototype.o=function(g){this.u(2,g)};
b.prototype.m=function(g){this.u(1,g)};
b.prototype.u=function(g,h){if(0!=this.h)throw Error("Cannot settle("+g+", "+h+"): Promise already settled in state"+this.h);this.h=g;this.j=h;2===this.h&&this.Y();this.A()};
b.prototype.Y=function(){var g=this;e(function(){if(g.K()){var h=da.console;"undefined"!==typeof h&&h.error(g.j)}},1)};
b.prototype.K=function(){if(this.D)return!1;var g=da.CustomEvent,h=da.Event,k=da.dispatchEvent;if("undefined"===typeof k)return!0;"function"===typeof g?g=new g("unhandledrejection",{cancelable:!0}):"function"===typeof h?g=new h("unhandledrejection",{cancelable:!0}):(g=da.document.createEvent("CustomEvent"),g.initCustomEvent("unhandledrejection",!1,!0,g));g.promise=this;g.reason=this.j;return k(g)};
b.prototype.A=function(){if(null!=this.i){for(var g=0;g<this.i.length;++g)f.i(this.i[g]);this.i=null}};
var f=new c;b.prototype.ga=function(g){var h=this.l();g.Ka(h.resolve,h.reject)};
b.prototype.qa=function(g,h){var k=this.l();try{g.call(h,k.resolve,k.reject)}catch(m){k.reject(m)}};
b.prototype.then=function(g,h){function k(x,u){return"function"==typeof x?function(C){try{m(x(C))}catch(D){n(D)}}:u}
var m,n,t=new b(function(x,u){m=x;n=u});
this.Ka(k(g,m),k(h,n));return t};
b.prototype.catch=function(g){return this.then(void 0,g)};
b.prototype.Ka=function(g,h){function k(){switch(m.h){case 1:g(m.j);break;case 2:h(m.j);break;default:throw Error("Unexpected state: "+m.h);}}
var m=this;null==this.i?f.i(k):this.i.push(k);this.D=!0};
b.resolve=d;b.reject=function(g){return new b(function(h,k){k(g)})};
b.race=function(g){return new b(function(h,k){for(var m=q(g),n=m.next();!n.done;n=m.next())d(n.value).Ka(h,k)})};
b.all=function(g){var h=q(g),k=h.next();return k.done?d([]):new b(function(m,n){function t(C){return function(D){x[C]=D;u--;0==u&&m(x)}}
var x=[],u=0;do x.push(void 0),u++,d(k.value).Ka(t(x.length-1),n),k=h.next();while(!k.done)})};
return b});
function Ea(a,b){return Object.prototype.hasOwnProperty.call(a,b)}
p("WeakMap",function(a){function b(k){this.h=(h+=Math.random()+1).toString();if(k){k=q(k);for(var m;!(m=k.next()).done;)m=m.value,this.set(m[0],m[1])}}
function c(){}
function d(k){var m=typeof k;return"object"===m&&null!==k||"function"===m}
function e(k){if(!Ea(k,g)){var m=new c;ba(k,g,{value:m})}}
function f(k){var m=Object[k];m&&(Object[k]=function(n){if(n instanceof c)return n;Object.isExtensible(n)&&e(n);return m(n)})}
if(function(){if(!a||!Object.seal)return!1;try{var k=Object.seal({}),m=Object.seal({}),n=new a([[k,2],[m,3]]);if(2!=n.get(k)||3!=n.get(m))return!1;n.delete(k);n.set(m,4);return!n.has(k)&&4==n.get(m)}catch(t){return!1}}())return a;
var g="$jscomp_hidden_"+Math.random();f("freeze");f("preventExtensions");f("seal");var h=0;b.prototype.set=function(k,m){if(!d(k))throw Error("Invalid WeakMap key");e(k);if(!Ea(k,g))throw Error("WeakMap key fail: "+k);k[g][this.h]=m;return this};
b.prototype.get=function(k){return d(k)&&Ea(k,g)?k[g][this.h]:void 0};
b.prototype.has=function(k){return d(k)&&Ea(k,g)&&Ea(k[g],this.h)};
b.prototype.delete=function(k){return d(k)&&Ea(k,g)&&Ea(k[g],this.h)?delete k[g][this.h]:!1};
return b});
p("Map",function(a){function b(){var h={};return h.previous=h.next=h.head=h}
function c(h,k){var m=h.h;return ea(function(){if(m){for(;m.head!=h.h;)m=m.previous;for(;m.next!=m.head;)return m=m.next,{done:!1,value:k(m)};m=null}return{done:!0,value:void 0}})}
function d(h,k){var m=k&&typeof k;"object"==m||"function"==m?f.has(k)?m=f.get(k):(m=""+ ++g,f.set(k,m)):m="p_"+k;var n=h.data_[m];if(n&&Ea(h.data_,m))for(h=0;h<n.length;h++){var t=n[h];if(k!==k&&t.key!==t.key||k===t.key)return{id:m,list:n,index:h,entry:t}}return{id:m,list:n,index:-1,entry:void 0}}
function e(h){this.data_={};this.h=b();this.size=0;if(h){h=q(h);for(var k;!(k=h.next()).done;)k=k.value,this.set(k[0],k[1])}}
if(function(){if(!a||"function"!=typeof a||!a.prototype.entries||"function"!=typeof Object.seal)return!1;try{var h=Object.seal({x:4}),k=new a(q([[h,"s"]]));if("s"!=k.get(h)||1!=k.size||k.get({x:4})||k.set({x:4},"t")!=k||2!=k.size)return!1;var m=k.entries(),n=m.next();if(n.done||n.value[0]!=h||"s"!=n.value[1])return!1;n=m.next();return n.done||4!=n.value[0].x||"t"!=n.value[1]||!m.next().done?!1:!0}catch(t){return!1}}())return a;
var f=new WeakMap;e.prototype.set=function(h,k){h=0===h?0:h;var m=d(this,h);m.list||(m.list=this.data_[m.id]=[]);m.entry?m.entry.value=k:(m.entry={next:this.h,previous:this.h.previous,head:this.h,key:h,value:k},m.list.push(m.entry),this.h.previous.next=m.entry,this.h.previous=m.entry,this.size++);return this};
e.prototype.delete=function(h){h=d(this,h);return h.entry&&h.list?(h.list.splice(h.index,1),h.list.length||delete this.data_[h.id],h.entry.previous.next=h.entry.next,h.entry.next.previous=h.entry.previous,h.entry.head=null,this.size--,!0):!1};
e.prototype.clear=function(){this.data_={};this.h=this.h.previous=b();this.size=0};
e.prototype.has=function(h){return!!d(this,h).entry};
e.prototype.get=function(h){return(h=d(this,h).entry)&&h.value};
e.prototype.entries=function(){return c(this,function(h){return[h.key,h.value]})};
e.prototype.keys=function(){return c(this,function(h){return h.key})};
e.prototype.values=function(){return c(this,function(h){return h.value})};
e.prototype.forEach=function(h,k){for(var m=this.entries(),n;!(n=m.next()).done;)n=n.value,h.call(k,n[1],n[0],this)};
e.prototype[Symbol.iterator]=e.prototype.entries;var g=0;return e});
function Fa(a,b,c){if(null==a)throw new TypeError("The 'this' value for String.prototype."+c+" must not be null or undefined");if(b instanceof RegExp)throw new TypeError("First argument to String.prototype."+c+" must not be a regular expression");return a+""}
p("String.prototype.endsWith",function(a){return a?a:function(b,c){var d=Fa(this,b,"endsWith");b+="";void 0===c&&(c=d.length);c=Math.max(0,Math.min(c|0,d.length));for(var e=b.length;0<e&&0<c;)if(d[--c]!=b[--e])return!1;return 0>=e}});
p("Array.prototype.find",function(a){return a?a:function(b,c){a:{var d=this;d instanceof String&&(d=String(d));for(var e=d.length,f=0;f<e;f++){var g=d[f];if(b.call(c,g,f,d)){b=g;break a}}b=void 0}return b}});
p("String.prototype.startsWith",function(a){return a?a:function(b,c){var d=Fa(this,b,"startsWith");b+="";var e=d.length,f=b.length;c=Math.max(0,Math.min(c|0,d.length));for(var g=0;g<f&&c<e;)if(d[c++]!=b[g++])return!1;return g>=f}});
function Ga(a,b){a instanceof String&&(a+="");var c=0,d=!1,e={next:function(){if(!d&&c<a.length){var f=c++;return{value:b(f,a[f]),done:!1}}d=!0;return{done:!0,value:void 0}}};
e[Symbol.iterator]=function(){return e};
return e}
p("Array.prototype.entries",function(a){return a?a:function(){return Ga(this,function(b,c){return[b,c]})}});
p("Object.setPrototypeOf",function(a){return a||oa});
var Ha="function"==typeof Object.assign?Object.assign:function(a,b){for(var c=1;c<arguments.length;c++){var d=arguments[c];if(d)for(var e in d)Ea(d,e)&&(a[e]=d[e])}return a};
p("Object.assign",function(a){return a||Ha});
p("Set",function(a){function b(c){this.h=new Map;if(c){c=q(c);for(var d;!(d=c.next()).done;)this.add(d.value)}this.size=this.h.size}
if(function(){if(!a||"function"!=typeof a||!a.prototype.entries||"function"!=typeof Object.seal)return!1;try{var c=Object.seal({x:4}),d=new a(q([c]));if(!d.has(c)||1!=d.size||d.add(c)!=d||1!=d.size||d.add({x:4})!=d||2!=d.size)return!1;var e=d.entries(),f=e.next();if(f.done||f.value[0]!=c||f.value[1]!=c)return!1;f=e.next();return f.done||f.value[0]==c||4!=f.value[0].x||f.value[1]!=f.value[0]?!1:e.next().done}catch(g){return!1}}())return a;
b.prototype.add=function(c){c=0===c?0:c;this.h.set(c,c);this.size=this.h.size;return this};
b.prototype.delete=function(c){c=this.h.delete(c);this.size=this.h.size;return c};
b.prototype.clear=function(){this.h.clear();this.size=0};
b.prototype.has=function(c){return this.h.has(c)};
b.prototype.entries=function(){return this.h.entries()};
b.prototype.values=function(){return this.h.values()};
b.prototype.keys=b.prototype.values;b.prototype[Symbol.iterator]=b.prototype.values;b.prototype.forEach=function(c,d){var e=this;this.h.forEach(function(f){return c.call(d,f,f,e)})};
return b});
p("Object.entries",function(a){return a?a:function(b){var c=[],d;for(d in b)Ea(b,d)&&c.push([d,b[d]]);return c}});
p("Array.prototype.keys",function(a){return a?a:function(){return Ga(this,function(b){return b})}});
p("Array.prototype.values",function(a){return a?a:function(){return Ga(this,function(b,c){return c})}});
p("Object.is",function(a){return a?a:function(b,c){return b===c?0!==b||1/b===1/c:b!==b&&c!==c}});
p("Array.prototype.includes",function(a){return a?a:function(b,c){var d=this;d instanceof String&&(d=String(d));var e=d.length;c=c||0;for(0>c&&(c=Math.max(c+e,0));c<e;c++){var f=d[c];if(f===b||Object.is(f,b))return!0}return!1}});
p("String.prototype.includes",function(a){return a?a:function(b,c){return-1!==Fa(this,b,"includes").indexOf(b,c||0)}});
p("Array.from",function(a){return a?a:function(b,c,d){c=null!=c?c:function(h){return h};
var e=[],f="undefined"!=typeof Symbol&&Symbol.iterator&&b[Symbol.iterator];if("function"==typeof f){b=f.call(b);for(var g=0;!(f=b.next()).done;)e.push(c.call(d,f.value,g++))}else for(f=b.length,g=0;g<f;g++)e.push(c.call(d,b[g],g));return e}});
p("Number.isNaN",function(a){return a?a:function(b){return"number"===typeof b&&isNaN(b)}});
p("Number.MAX_SAFE_INTEGER",function(){return 9007199254740991});
p("Object.values",function(a){return a?a:function(b){var c=[],d;for(d in b)Ea(b,d)&&c.push(b[d]);return c}});
var y=this||self;function z(a,b,c){a=a.split(".");c=c||y;a[0]in c||"undefined"==typeof c.execScript||c.execScript("var "+a[0]);for(var d;a.length&&(d=a.shift());)a.length||void 0===b?c[d]&&c[d]!==Object.prototype[d]?c=c[d]:c=c[d]={}:c[d]=b}
function A(a,b){a=a.split(".");b=b||y;for(var c=0;c<a.length;c++)if(b=b[a[c]],null==b)return null;return b}
function Ia(){}
function Ja(a){a.ma=void 0;a.getInstance=function(){return a.ma?a.ma:a.ma=new a}}
function Ka(a){var b=typeof a;return"object"!=b?b:a?Array.isArray(a)?"array":b:"null"}
function La(a){var b=Ka(a);return"array"==b||"object"==b&&"number"==typeof a.length}
function Ma(a){var b=typeof a;return"object"==b&&null!=a||"function"==b}
function Na(a){return Object.prototype.hasOwnProperty.call(a,Oa)&&a[Oa]||(a[Oa]=++Pa)}
var Oa="closure_uid_"+(1E9*Math.random()>>>0),Pa=0;function Ra(a,b,c){return a.call.apply(a.bind,arguments)}
function Sa(a,b,c){if(!a)throw Error();if(2<arguments.length){var d=Array.prototype.slice.call(arguments,2);return function(){var e=Array.prototype.slice.call(arguments);Array.prototype.unshift.apply(e,d);return a.apply(b,e)}}return function(){return a.apply(b,arguments)}}
function Ta(a,b,c){Function.prototype.bind&&-1!=Function.prototype.bind.toString().indexOf("native code")?Ta=Ra:Ta=Sa;return Ta.apply(null,arguments)}
function Ua(a,b){var c=Array.prototype.slice.call(arguments,1);return function(){var d=c.slice();d.push.apply(d,arguments);return a.apply(this,d)}}
function Va(a,b){z(a,b,void 0)}
function Wa(a,b){function c(){}
c.prototype=b.prototype;a.Z=b.prototype;a.prototype=new c;a.prototype.constructor=a;a.oo=function(d,e,f){for(var g=Array(arguments.length-2),h=2;h<arguments.length;h++)g[h-2]=arguments[h];return b.prototype[e].apply(d,g)}}
function Xa(a){return a}
;function Za(a,b){if(Error.captureStackTrace)Error.captureStackTrace(this,Za);else{var c=Error().stack;c&&(this.stack=c)}a&&(this.message=String(a));void 0!==b&&(this.Pb=b)}
Wa(Za,Error);Za.prototype.name="CustomError";function $a(a){a=a.url;var b=/[?&]dsh=1(&|$)/.test(a);this.j=!b&&/[?&]ae=1(&|$)/.test(a);this.l=!b&&/[?&]ae=2(&|$)/.test(a);if((this.h=/[?&]adurl=([^&]*)/.exec(a))&&this.h[1]){try{var c=decodeURIComponent(this.h[1])}catch(d){c=null}this.i=c}}
;function ab(a){var b=!1,c;return function(){b||(c=a(),b=!0);return c}}
;var bb=Array.prototype.indexOf?function(a,b){return Array.prototype.indexOf.call(a,b,void 0)}:function(a,b){if("string"===typeof a)return"string"!==typeof b||1!=b.length?-1:a.indexOf(b,0);
for(var c=0;c<a.length;c++)if(c in a&&a[c]===b)return c;return-1},cb=Array.prototype.forEach?function(a,b,c){Array.prototype.forEach.call(a,b,c)}:function(a,b,c){for(var d=a.length,e="string"===typeof a?a.split(""):a,f=0;f<d;f++)f in e&&b.call(c,e[f],f,a)},db=Array.prototype.filter?function(a,b){return Array.prototype.filter.call(a,b,void 0)}:function(a,b){for(var c=a.length,d=[],e=0,f="string"===typeof a?a.split(""):a,g=0;g<c;g++)if(g in f){var h=f[g];
b.call(void 0,h,g,a)&&(d[e++]=h)}return d},eb=Array.prototype.map?function(a,b){return Array.prototype.map.call(a,b,void 0)}:function(a,b){for(var c=a.length,d=Array(c),e="string"===typeof a?a.split(""):a,f=0;f<c;f++)f in e&&(d[f]=b.call(void 0,e[f],f,a));
return d},fb=Array.prototype.reduce?function(a,b,c){return Array.prototype.reduce.call(a,b,c)}:function(a,b,c){var d=c;
cb(a,function(e,f){d=b.call(void 0,d,e,f,a)});
return d};
function gb(a,b){a:{for(var c=a.length,d="string"===typeof a?a.split(""):a,e=0;e<c;e++)if(e in d&&b.call(void 0,d[e],e,a)){b=e;break a}b=-1}return 0>b?null:"string"===typeof a?a.charAt(b):a[b]}
function ib(a,b){b=bb(a,b);var c;(c=0<=b)&&Array.prototype.splice.call(a,b,1);return c}
function jb(a,b){for(var c=1;c<arguments.length;c++){var d=arguments[c];if(La(d)){var e=a.length||0,f=d.length||0;a.length=e+f;for(var g=0;g<f;g++)a[e+g]=d[g]}else a.push(d)}}
;function kb(a,b){for(var c in a)b.call(void 0,a[c],c,a)}
function lb(a){var b=mb,c;for(c in b)if(a.call(void 0,b[c],c,b))return c}
function nb(a){for(var b in a)return!1;return!0}
function ob(a,b){if(null!==a&&b in a)throw Error('The object already contains the key "'+b+'"');a[b]=!0}
function pb(){var a=B("PLAYER_VARS",{});return null!==a&&"privembed"in a?a.privembed:!1}
function qb(a,b){for(var c in a)if(!(c in b)||a[c]!==b[c])return!1;for(var d in b)if(!(d in a))return!1;return!0}
function rb(a){var b={},c;for(c in a)b[c]=a[c];return b}
function sb(a){if(!a||"object"!==typeof a)return a;if("function"===typeof a.clone)return a.clone();if("undefined"!==typeof Map&&a instanceof Map)return new Map(a);if("undefined"!==typeof Set&&a instanceof Set)return new Set(a);var b=Array.isArray(a)?[]:"function"!==typeof ArrayBuffer||"function"!==typeof ArrayBuffer.isView||!ArrayBuffer.isView(a)||a instanceof DataView?{}:new a.constructor(a.length),c;for(c in a)b[c]=sb(a[c]);return b}
var tb="constructor hasOwnProperty isPrototypeOf propertyIsEnumerable toLocaleString toString valueOf".split(" ");function ub(a,b){for(var c,d,e=1;e<arguments.length;e++){d=arguments[e];for(c in d)a[c]=d[c];for(var f=0;f<tb.length;f++)c=tb[f],Object.prototype.hasOwnProperty.call(d,c)&&(a[c]=d[c])}}
;var vb;function wb(){if(void 0===vb){var a=null,b=y.trustedTypes;if(b&&b.createPolicy){try{a=b.createPolicy("goog#html",{createHTML:Xa,createScript:Xa,createScriptURL:Xa})}catch(c){y.console&&y.console.error(c.message)}vb=a}else vb=a}return vb}
;function xb(a,b){this.j=a===yb&&b||""}
xb.prototype.i=!0;xb.prototype.h=function(){return this.j};
function Ab(a){return new xb(yb,a)}
var yb={};Ab("");var Bb={};function Cb(a){this.j=Bb===Bb?a:"";this.i=!0}
Cb.prototype.h=function(){return this.j.toString()};
Cb.prototype.toString=function(){return this.j.toString()};function Db(a,b){this.j=b===Eb?a:""}
Db.prototype.i=!0;Db.prototype.h=function(){return this.j.toString()};
Db.prototype.toString=function(){return this.j+""};
function Fb(a){if(a instanceof Db&&a.constructor===Db)return a.j;Ka(a);return"type_error:TrustedResourceUrl"}
var Eb={};function Gb(a){var b=wb();a=b?b.createScriptURL(a):a;return new Db(a,Eb)}
;var Hb=String.prototype.trim?function(a){return a.trim()}:function(a){return/^[\s\xa0]*([\s\S]*?)[\s\xa0]*$/.exec(a)[1]};function Ib(a,b){this.j=b===Jb?a:""}
Ib.prototype.i=!0;Ib.prototype.h=function(){return this.j.toString()};
Ib.prototype.toString=function(){return this.j.toString()};
function Kb(a){if(a instanceof Ib&&a.constructor===Ib)return a.j;Ka(a);return"type_error:SafeUrl"}
var Lb=/^(?:(?:https?|mailto|ftp):|[^:/?#]*(?:[/?#]|$))/i,Jb={};function Mb(){var a=y.navigator;return a&&(a=a.userAgent)?a:""}
function E(a){return-1!=Mb().indexOf(a)}
;function Nb(){return(E("Chrome")||E("CriOS"))&&!E("Edge")||E("Silk")}
;var Ob={};function Pb(a){this.j=Ob===Ob?a:"";this.i=!0}
Pb.prototype.h=function(){return this.j.toString()};
Pb.prototype.toString=function(){return this.j.toString()};function Qb(a,b){b instanceof Ib||b instanceof Ib||(b="object"==typeof b&&b.i?b.h():String(b),Lb.test(b)||(b="about:invalid#zClosurez"),b=new Ib(b,Jb));a.href=Kb(b)}
function Rb(a,b){a.rel="stylesheet";a.href=Fb(b).toString();(b=Sb('style[nonce],link[rel="stylesheet"][nonce]',a.ownerDocument&&a.ownerDocument.defaultView))&&a.setAttribute("nonce",b)}
function Tb(){return Sb("script[nonce]",void 0)}
var Ub=/^[\w+/_-]+[=]{0,2}$/;function Sb(a,b){b=(b||y).document;return b.querySelector?(a=b.querySelector(a))&&(a=a.nonce||a.getAttribute("nonce"))&&Ub.test(a)?a:"":""}
;function Vb(a){for(var b=0,c=0;c<a.length;++c)b=31*b+a.charCodeAt(c)>>>0;return b}
;var Wb=RegExp("^(?:([^:/?#.]+):)?(?://(?:([^\\\\/?#]*)@)?([^\\\\/?#]*?)(?::([0-9]+))?(?=[\\\\/?#]|$))?([^?#]+)?(?:\\?([^#]*))?(?:#([\\s\\S]*))?$");function Xb(a){return a?decodeURI(a):a}
function Yb(a){return Xb(a.match(Wb)[3]||null)}
function Zb(a){var b=a.match(Wb);a=b[5];var c=b[6];b=b[7];var d="";a&&(d+=a);c&&(d+="?"+c);b&&(d+="#"+b);return d}
function $b(a,b,c){if(Array.isArray(b))for(var d=0;d<b.length;d++)$b(a,String(b[d]),c);else null!=b&&c.push(a+(""===b?"":"="+encodeURIComponent(String(b))))}
function ac(a){var b=[],c;for(c in a)$b(c,a[c],b);return b.join("&")}
function bc(a,b){b=ac(b);if(b){var c=a.indexOf("#");0>c&&(c=a.length);var d=a.indexOf("?");if(0>d||d>c){d=c;var e=""}else e=a.substring(d+1,c);a=[a.substr(0,d),e,a.substr(c)];c=a[1];a[1]=b?c?c+"&"+b:b:c;b=a[0]+(a[1]?"?"+a[1]:"")+a[2]}else b=a;return b}
var cc=/#|$/;function dc(){return E("iPhone")&&!E("iPod")&&!E("iPad")}
;function ec(a){ec[" "](a);return a}
ec[" "]=Ia;var fc=E("Opera"),gc=E("Trident")||E("MSIE"),hc=E("Edge"),ic=E("Gecko")&&!(-1!=Mb().toLowerCase().indexOf("webkit")&&!E("Edge"))&&!(E("Trident")||E("MSIE"))&&!E("Edge"),jc=-1!=Mb().toLowerCase().indexOf("webkit")&&!E("Edge"),kc=E("Android");function lc(){var a=y.document;return a?a.documentMode:void 0}
var mc;a:{var nc="",oc=function(){var a=Mb();if(ic)return/rv:([^\);]+)(\)|;)/.exec(a);if(hc)return/Edge\/([\d\.]+)/.exec(a);if(gc)return/\b(?:MSIE|rv)[: ]([^\);]+)(\)|;)/.exec(a);if(jc)return/WebKit\/(\S+)/.exec(a);if(fc)return/(?:Version)[ \/]?(\S+)/.exec(a)}();
oc&&(nc=oc?oc[1]:"");if(gc){var pc=lc();if(null!=pc&&pc>parseFloat(nc)){mc=String(pc);break a}}mc=nc}var qc=mc,rc;if(y.document&&gc){var sc=lc();rc=sc?sc:parseInt(qc,10)||void 0}else rc=void 0;var tc=rc;var uc=dc()||E("iPod"),vc=E("iPad");!E("Android")||Nb();Nb();var wc=E("Safari")&&!(Nb()||E("Coast")||E("Opera")||E("Edge")||E("Edg/")||E("OPR")||E("Firefox")||E("FxiOS")||E("Silk")||E("Android"))&&!(dc()||E("iPad")||E("iPod"));var xc={},yc=null;
function zc(a,b){La(a);void 0===b&&(b=0);if(!yc){yc={};for(var c="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".split(""),d=["+/=","+/","-_=","-_.","-_"],e=0;5>e;e++){var f=c.concat(d[e].split(""));xc[e]=f;for(var g=0;g<f.length;g++){var h=f[g];void 0===yc[h]&&(yc[h]=g)}}}b=xc[b];c=Array(Math.floor(a.length/3));d=b[64]||"";for(e=f=0;f<a.length-2;f+=3){var k=a[f],m=a[f+1];h=a[f+2];g=b[k>>2];k=b[(k&3)<<4|m>>4];m=b[(m&15)<<2|h>>6];h=b[h&63];c[e++]=""+g+k+m+h}g=0;h=d;switch(a.length-
f){case 2:g=a[f+1],h=b[(g&15)<<2]||d;case 1:a=a[f],c[e]=""+b[a>>2]+b[(a&3)<<4|g>>4]+h+d}return c.join("")}
;var Ac="function"===typeof Uint8Array;var Bc="function"===typeof Symbol&&"symbol"===typeof Symbol()?Symbol(void 0):void 0;function Cc(a){Object.isFrozen(a)||(Bc?a[Bc]|=1:void 0!==a.h?a.h|=1:Object.defineProperties(a,{h:{value:1,configurable:!0,writable:!0,enumerable:!1}}));return a}
;function Dc(a){return null!==a&&"object"===typeof a&&!Array.isArray(a)&&a.constructor===Object}
var Ec;function Fc(a){switch(typeof a){case "number":return isFinite(a)?a:String(a);case "object":if(a&&!Array.isArray(a)&&Ac&&null!=a&&a instanceof Uint8Array)return zc(a)}return a}
;function Gc(a,b){if(null!=a){if(Array.isArray(a))a=Hc(a,b);else if(Dc(a)){var c={},d;for(d in a)c[d]=Gc(a[d],b);a=c}else a=b(a);return a}}
function Hc(a,b){for(var c=a.slice(),d=0;d<c.length;d++)c[d]=Gc(c[d],b);if(Array.isArray(a)){var e;Bc?e=a[Bc]:e=a.h;a=!!((null==e?0:e)&1)}else a=!1;a&&Cc(c);return c}
function Ic(a){if(a&&"object"==typeof a&&a.toJSON)return a.toJSON();a=Fc(a);return Array.isArray(a)?Hc(a,Ic):a}
function Jc(a){return Ac&&null!=a&&a instanceof Uint8Array?new Uint8Array(a):a}
;var Kc;function F(a,b,c){var d=Kc;Kc=null;a||(a=d);d=this.constructor.wo;a||(a=d?[d]:[]);this.j=(d?0:-1)-(this.constructor.uo||0);this.h=void 0;this.N=a;a:{d=this.N.length;a=d-1;if(d&&(d=this.N[a],Dc(d))){this.l=a-this.j;this.i=d;break a}void 0!==b&&-1<b?(this.l=Math.max(b,a+1-this.j),this.i=void 0):this.l=Number.MAX_VALUE}if(c)for(b=0;b<c.length;b++)if(a=c[b],a<this.l)a+=this.j,(d=this.N[a])?Array.isArray(d)&&Cc(d):this.N[a]=Lc;else{d=this.i||(this.i=this.N[this.l+this.j]={});var e=d[a];e?Array.isArray(e)&&
Cc(e):d[a]=Lc}}
var Lc=Object.freeze(Cc([]));function Mc(a,b,c){return-1===b?null:b>=a.l?a.i?a.i[b]:void 0:(void 0===c?0:c)&&a.i&&(c=a.i[b],null!=c)?c:a.N[b+a.j]}
function Nc(a,b,c){c=void 0===c?!1:c;var d=Mc(a,b,c);null==d&&(d=Lc);d===Lc&&(d=Cc(d.slice()),G(a,b,d,c));return d}
function G(a,b,c,d){b<a.l&&(void 0===d||!d)?a.N[b+a.j]=c:(a.i||(a.i=a.N[a.l+a.j]={}))[b]=c;return a}
function Oc(a,b,c,d){(c=Pc(a,c))&&c!==b&&null!=d&&(a.h&&c in a.h&&(a.h[c]=void 0),G(a,c,void 0));return G(a,b,d)}
function Pc(a,b){for(var c=0,d=0;d<b.length;d++){var e=b[d];null!=Mc(a,e)&&(0!==c&&G(a,c,void 0,!1),c=e)}return c}
function Qc(a,b,c,d,e){if(-1===c)return null;a.h||(a.h={});var f=a.h[c];if(f)return f;e=Mc(a,c,void 0===e?!1:e);if(null==e&&!d)return f;b=new b(e);return a.h[c]=b}
function Rc(a,b,c,d){a.h||(a.h={});var e=a.h[c];if(!e){d=Nc(a,c,void 0===d?!1:d);e=[];for(var f=0;f<d.length;f++)e[f]=new b(d[f]);a.h[c]=e}return e}
function H(a,b,c,d){a.h||(a.h={});var e=c?c.N:c;a.h[b]=c;return G(a,b,e,void 0===d?!1:d)}
function Sc(a,b,c){var d=Tc;a.h||(a.h={});var e=c?c.N:c;a.h[b]=c;Oc(a,b,d,e)}
function Uc(a,b,c,d){var e=Rc(a,c,b,void 0===e?!1:e);c=d?d:new c;a=Nc(a,b);e.push(c);a.push(c.N)}
F.prototype.toJSON=function(){var a=this.N;return Ec?a:Hc(a,Ic)};
function Vc(a,b){return Fc(b)}
F.prototype.toString=function(){return this.N.toString()};
F.prototype.clone=function(){var a=this.constructor,b;Kc=b=Hc(this.N,Jc);a=new a(b);Kc=null;Wc(a,this);return a};
function Wc(a,b){b.o&&(a.o=b.o.slice());var c=b.h;if(c){b=b.i;for(var d in c){var e=c[d];if(e){var f=!(!b||!b[d]),g=+d;if(Array.isArray(e)){if(e.length)for(f=Rc(a,e[0].constructor,g,f),g=0;g<Math.min(f.length,e.length);g++)Wc(f[g],e[g])}else(f=Qc(a,e.constructor,g,void 0,f))&&Wc(f,e)}}}}
;function Xc(a,b){var c=this.h;if(this.isRepeated){var d=!0;d=void 0===d?!1:d;if(b){var e=Cc([]);for(var f=0;f<b.length;f++)e[f]=b[f].N;a.h||(a.h={});a.h[c]=b}else a.h&&(a.h[c]=void 0),e=Lc;a=G(a,c,e,d)}else a=H(a,c,b,!0);return a}
;function Yc(){var a=this;this.promise=new Promise(function(b,c){a.resolve=b;a.reject=c})}
;function Zc(a){this.i=!1;var b=a.program;a=a.globalName;var c=new Yc;this.j=c.promise;this.l=q((0,y[a].a)(b,function(d,e){Promise.resolve().then(function(){c.resolve({Ob:d,wc:e})})},!0)).next().value;
this.vc=c.promise.then(function(){})}
Zc.prototype.snapshot=function(a){if(this.i)throw Error("Already disposed");return this.j.then(function(b){var c=b.Ob;return new Promise(function(d){c(function(e){d(e)},[a.qb,
a.xc])})})};
Zc.prototype.Jb=function(a){if(this.i)throw Error("Already disposed");return this.l([a.qb,a.xc])};
Zc.prototype.dispose=function(){this.i=!0;this.j.then(function(a){(a=a.wc)&&a()})};
Zc.prototype.h=function(){return this.i};var $c=window;Ab("csi.gstatic.com");Ab("googleads.g.doubleclick.net");Ab("partner.googleadservices.com");Ab("pubads.g.doubleclick.net");Ab("securepubads.g.doubleclick.net");Ab("tpc.googlesyndication.com");/*

 SPDX-License-Identifier: Apache-2.0
*/
var ad={};function bd(){}
function cd(a){this.h=a}
r(cd,bd);cd.prototype.toString=function(){return this.h};
var dd=new cd("about:invalid#zTSz",ad);function ed(a){if(a instanceof bd)if(a instanceof cd)a=a.h;else throw Error("");else a=Kb(a);return a}
;function fd(a,b){a.src=Fb(b);var c;b=(a.ownerDocument&&a.ownerDocument.defaultView||window).document;var d=null===(c=b.querySelector)||void 0===c?void 0:c.call(b,"script[nonce]");(c=d?d.nonce||d.getAttribute("nonce")||"":"")&&a.setAttribute("nonce",c)}
;function gd(a,b){this.x=void 0!==a?a:0;this.y=void 0!==b?b:0}
l=gd.prototype;l.clone=function(){return new gd(this.x,this.y)};
l.equals=function(a){return a instanceof gd&&(this==a?!0:this&&a?this.x==a.x&&this.y==a.y:!1)};
l.ceil=function(){this.x=Math.ceil(this.x);this.y=Math.ceil(this.y);return this};
l.floor=function(){this.x=Math.floor(this.x);this.y=Math.floor(this.y);return this};
l.round=function(){this.x=Math.round(this.x);this.y=Math.round(this.y);return this};
l.scale=function(a,b){this.x*=a;this.y*="number"===typeof b?b:a;return this};function hd(a,b){this.width=a;this.height=b}
l=hd.prototype;l.clone=function(){return new hd(this.width,this.height)};
l.aspectRatio=function(){return this.width/this.height};
l.isEmpty=function(){return!(this.width*this.height)};
l.ceil=function(){this.width=Math.ceil(this.width);this.height=Math.ceil(this.height);return this};
l.floor=function(){this.width=Math.floor(this.width);this.height=Math.floor(this.height);return this};
l.round=function(){this.width=Math.round(this.width);this.height=Math.round(this.height);return this};
l.scale=function(a,b){this.width*=a;this.height*="number"===typeof b?b:a;return this};function id(a){var b=document;return"string"===typeof a?b.getElementById(a):a}
function jd(a){var b=document;a=String(a);"application/xhtml+xml"===b.contentType&&(a=a.toLowerCase());return b.createElement(a)}
function kd(a,b){for(var c=0;a;){if(b(a))return a;a=a.parentNode;c++}return null}
;function ld(a){var b=md;if(b)for(var c in b)Object.prototype.hasOwnProperty.call(b,c)&&a(b[c],c,b)}
function nd(){var a=[];ld(function(b){a.push(b)});
return a}
var md={Mc:"allow-forms",Nc:"allow-modals",Oc:"allow-orientation-lock",Pc:"allow-pointer-lock",Qc:"allow-popups",Rc:"allow-popups-to-escape-sandbox",Sc:"allow-presentation",Tc:"allow-same-origin",Uc:"allow-scripts",Vc:"allow-top-navigation",Wc:"allow-top-navigation-by-user-activation"},od=ab(function(){return nd()});
function pd(){var a=qd(),b={};cb(od(),function(c){a.sandbox&&a.sandbox.supports&&a.sandbox.supports(c)&&(b[c]=!0)});
return b}
function qd(){var a=void 0===a?document:a;return a.createElement("iframe")}
;function rd(a){this.isValid=a}
function sd(a){return new rd(function(b){return b.substr(0,a.length+1).toLowerCase()===a+":"})}
var td=[sd("data"),sd("http"),sd("https"),sd("mailto"),sd("ftp"),new rd(function(a){return/^[^:]*([/?#]|$)/.test(a)})];function ud(a){"number"==typeof a&&(a=Math.round(a)+"px");return a}
;var vd=(new Date).getTime();var wd=new function(a,b){this.flag=a;this.defaultValue=void 0===b?!1:b}(1959);function xd(a){if(!a)return"";if(/^about:(?:blank|srcdoc)$/.test(a))return window.origin||"";a=a.split("#")[0].split("?")[0];a=a.toLowerCase();0==a.indexOf("//")&&(a=window.location.protocol+a);/^[\w\-]*:\/\//.test(a)||(a=window.location.href);var b=a.substring(a.indexOf("://")+3),c=b.indexOf("/");-1!=c&&(b=b.substring(0,c));c=a.substring(0,a.indexOf("://"));if(!c)throw Error("URI is missing protocol: "+a);if("http"!==c&&"https"!==c&&"chrome-extension"!==c&&"moz-extension"!==c&&"file"!==c&&"android-app"!==
c&&"chrome-search"!==c&&"chrome-untrusted"!==c&&"chrome"!==c&&"app"!==c&&"devtools"!==c)throw Error("Invalid URI scheme in origin: "+c);a="";var d=b.indexOf(":");if(-1!=d){var e=b.substring(d+1);b=b.substring(0,d);if("http"===c&&"80"!==e||"https"===c&&"443"!==e)a=":"+e}return c+"://"+b+a}
;function yd(){function a(){e[0]=1732584193;e[1]=4023233417;e[2]=2562383102;e[3]=271733878;e[4]=3285377520;n=m=0}
function b(t){for(var x=g,u=0;64>u;u+=4)x[u/4]=t[u]<<24|t[u+1]<<16|t[u+2]<<8|t[u+3];for(u=16;80>u;u++)t=x[u-3]^x[u-8]^x[u-14]^x[u-16],x[u]=(t<<1|t>>>31)&4294967295;t=e[0];var C=e[1],D=e[2],K=e[3],N=e[4];for(u=0;80>u;u++){if(40>u)if(20>u){var S=K^C&(D^K);var W=1518500249}else S=C^D^K,W=1859775393;else 60>u?(S=C&D|K&(C|D),W=2400959708):(S=C^D^K,W=3395469782);S=((t<<5|t>>>27)&4294967295)+S+N+W+x[u]&4294967295;N=K;K=D;D=(C<<30|C>>>2)&4294967295;C=t;t=S}e[0]=e[0]+t&4294967295;e[1]=e[1]+C&4294967295;e[2]=
e[2]+D&4294967295;e[3]=e[3]+K&4294967295;e[4]=e[4]+N&4294967295}
function c(t,x){if("string"===typeof t){t=unescape(encodeURIComponent(t));for(var u=[],C=0,D=t.length;C<D;++C)u.push(t.charCodeAt(C));t=u}x||(x=t.length);u=0;if(0==m)for(;u+64<x;)b(t.slice(u,u+64)),u+=64,n+=64;for(;u<x;)if(f[m++]=t[u++],n++,64==m)for(m=0,b(f);u+64<x;)b(t.slice(u,u+64)),u+=64,n+=64}
function d(){var t=[],x=8*n;56>m?c(h,56-m):c(h,64-(m-56));for(var u=63;56<=u;u--)f[u]=x&255,x>>>=8;b(f);for(u=x=0;5>u;u++)for(var C=24;0<=C;C-=8)t[x++]=e[u]>>C&255;return t}
for(var e=[],f=[],g=[],h=[128],k=1;64>k;++k)h[k]=0;var m,n;a();return{reset:a,update:c,digest:d,Sb:function(){for(var t=d(),x="",u=0;u<t.length;u++)x+="0123456789ABCDEF".charAt(Math.floor(t[u]/16))+"0123456789ABCDEF".charAt(t[u]%16);return x}}}
;function zd(a,b,c){var d=String(y.location.href);return d&&a&&b?[b,Ad(xd(d),a,c||null)].join(" "):null}
function Ad(a,b,c){var d=[],e=[];if(1==(Array.isArray(c)?2:1))return e=[b,a],cb(d,function(h){e.push(h)}),Bd(e.join(" "));
var f=[],g=[];cb(c,function(h){g.push(h.key);f.push(h.value)});
c=Math.floor((new Date).getTime()/1E3);e=0==f.length?[c,b,a]:[f.join(":"),c,b,a];cb(d,function(h){e.push(h)});
a=Bd(e.join(" "));a=[c,a];0==g.length||a.push(g.join(""));return a.join("_")}
function Bd(a){var b=yd();b.update(a);return b.Sb().toLowerCase()}
;var Cd={};function Dd(a){this.h=a||{cookie:""}}
l=Dd.prototype;l.isEnabled=function(){if(!y.navigator.cookieEnabled)return!1;if(!this.isEmpty())return!0;this.set("TESTCOOKIESENABLED","1",{Qa:60});if("1"!==this.get("TESTCOOKIESENABLED"))return!1;this.remove("TESTCOOKIESENABLED");return!0};
l.set=function(a,b,c){var d=!1;if("object"===typeof c){var e=c.Do;d=c.secure||!1;var f=c.domain||void 0;var g=c.path||void 0;var h=c.Qa}if(/[;=\s]/.test(a))throw Error('Invalid cookie name "'+a+'"');if(/[;\r\n]/.test(b))throw Error('Invalid cookie value "'+b+'"');void 0===h&&(h=-1);c=f?";domain="+f:"";g=g?";path="+g:"";d=d?";secure":"";h=0>h?"":0==h?";expires="+(new Date(1970,1,1)).toUTCString():";expires="+(new Date(Date.now()+1E3*h)).toUTCString();this.h.cookie=a+"="+b+c+g+h+d+(null!=e?";samesite="+
e:"")};
l.get=function(a,b){for(var c=a+"=",d=(this.h.cookie||"").split(";"),e=0,f;e<d.length;e++){f=Hb(d[e]);if(0==f.lastIndexOf(c,0))return f.substr(c.length);if(f==a)return""}return b};
l.remove=function(a,b,c){var d=void 0!==this.get(a);this.set(a,"",{Qa:0,path:b,domain:c});return d};
l.ab=function(){return Ed(this).keys};
l.isEmpty=function(){return!this.h.cookie};
l.clear=function(){for(var a=Ed(this).keys,b=a.length-1;0<=b;b--)this.remove(a[b])};
function Ed(a){a=(a.h.cookie||"").split(";");for(var b=[],c=[],d,e,f=0;f<a.length;f++)e=Hb(a[f]),d=e.indexOf("="),-1==d?(b.push(""),c.push(e)):(b.push(e.substring(0,d)),c.push(e.substring(d+1)));return{keys:b,values:c}}
var Fd=new Dd("undefined"==typeof document?null:document);function Gd(a){return!!Cd.FPA_SAMESITE_PHASE2_MOD||!(void 0===a||!a)}
function Hd(a){a=void 0===a?!1:a;var b=y.__SAPISID||y.__APISID||y.__3PSAPISID||y.__OVERRIDE_SID;Gd(a)&&(b=b||y.__1PSAPISID);if(b)return!0;var c=new Dd(document);b=c.get("SAPISID")||c.get("APISID")||c.get("__Secure-3PAPISID")||c.get("SID");Gd(a)&&(b=b||c.get("__Secure-1PAPISID"));return!!b}
function Id(a,b,c,d){(a=y[a])||(a=(new Dd(document)).get(b));return a?zd(a,c,d):null}
function Jd(a){var b=void 0===b?!1:b;var c=xd(String(y.location.href)),d=[];if(Hd(b)){c=0==c.indexOf("https:")||0==c.indexOf("chrome-extension:")||0==c.indexOf("moz-extension:");var e=c?y.__SAPISID:y.__APISID;e||(e=new Dd(document),e=e.get(c?"SAPISID":"APISID")||e.get("__Secure-3PAPISID"));(e=e?zd(e,c?"SAPISIDHASH":"APISIDHASH",a):null)&&d.push(e);c&&Gd(b)&&((b=Id("__1PSAPISID","__Secure-1PAPISID","SAPISID1PHASH",a))&&d.push(b),(a=Id("__3PSAPISID","__Secure-3PAPISID","SAPISID3PHASH",a))&&d.push(a))}return 0==
d.length?null:d.join(" ")}
;function Kd(a){a&&"function"==typeof a.dispose&&a.dispose()}
;function Ld(a){for(var b=0,c=arguments.length;b<c;++b){var d=arguments[b];La(d)?Ld.apply(null,d):Kd(d)}}
;function I(){this.D=this.D;this.o=this.o}
I.prototype.D=!1;I.prototype.h=function(){return this.D};
I.prototype.dispose=function(){this.D||(this.D=!0,this.H())};
function Md(a,b){a.D?b():(a.o||(a.o=[]),a.o.push(b))}
I.prototype.H=function(){if(this.o)for(;this.o.length;)this.o.shift()()};function Nd(a,b){this.type=a;this.h=this.target=b;this.defaultPrevented=this.j=!1}
Nd.prototype.stopPropagation=function(){this.j=!0};
Nd.prototype.preventDefault=function(){this.defaultPrevented=!0};function Od(a){var b=A("window.location.href");null==a&&(a='Unknown Error of type "null/undefined"');if("string"===typeof a)return{message:a,name:"Unknown error",lineNumber:"Not available",fileName:b,stack:"Not available"};var c=!1;try{var d=a.lineNumber||a.line||"Not available"}catch(g){d="Not available",c=!0}try{var e=a.fileName||a.filename||a.sourceURL||y.$googDebugFname||b}catch(g){e="Not available",c=!0}b=Pd(a);if(!(!c&&a.lineNumber&&a.fileName&&a.stack&&a.message&&a.name)){c=a.message;if(null==
c){if(a.constructor&&a.constructor instanceof Function){if(a.constructor.name)c=a.constructor.name;else if(c=a.constructor,Qd[c])c=Qd[c];else{c=String(c);if(!Qd[c]){var f=/function\s+([^\(]+)/m.exec(c);Qd[c]=f?f[1]:"[Anonymous]"}c=Qd[c]}c='Unknown Error of type "'+c+'"'}else c="Unknown Error of unknown type";"function"===typeof a.toString&&Object.prototype.toString!==a.toString&&(c+=": "+a.toString())}return{message:c,name:a.name||"UnknownError",lineNumber:d,fileName:e,stack:b||"Not available"}}a.stack=
b;return{message:a.message,name:a.name,lineNumber:a.lineNumber,fileName:a.fileName,stack:a.stack}}
function Pd(a,b){b||(b={});b[Rd(a)]=!0;var c=a.stack||"";(a=a.Pb)&&!b[Rd(a)]&&(c+="\nCaused by: ",a.stack&&0==a.stack.indexOf(a.toString())||(c+="string"===typeof a?a:a.message+"\n"),c+=Pd(a,b));return c}
function Rd(a){var b="";"function"===typeof a.toString&&(b=""+a);return b+a.stack}
var Qd={};var Sd=function(){if(!y.addEventListener||!Object.defineProperty)return!1;var a=!1,b=Object.defineProperty({},"passive",{get:function(){a=!0}});
try{y.addEventListener("test",Ia,b),y.removeEventListener("test",Ia,b)}catch(c){}return a}();function Td(a,b){Nd.call(this,a?a.type:"");this.relatedTarget=this.h=this.target=null;this.button=this.screenY=this.screenX=this.clientY=this.clientX=0;this.key="";this.charCode=this.keyCode=0;this.metaKey=this.shiftKey=this.altKey=this.ctrlKey=!1;this.state=null;this.pointerId=0;this.pointerType="";this.i=null;a&&this.init(a,b)}
Wa(Td,Nd);var Ud={2:"touch",3:"pen",4:"mouse"};
Td.prototype.init=function(a,b){var c=this.type=a.type,d=a.changedTouches&&a.changedTouches.length?a.changedTouches[0]:null;this.target=a.target||a.srcElement;this.h=b;if(b=a.relatedTarget){if(ic){a:{try{ec(b.nodeName);var e=!0;break a}catch(f){}e=!1}e||(b=null)}}else"mouseover"==c?b=a.fromElement:"mouseout"==c&&(b=a.toElement);this.relatedTarget=b;d?(this.clientX=void 0!==d.clientX?d.clientX:d.pageX,this.clientY=void 0!==d.clientY?d.clientY:d.pageY,this.screenX=d.screenX||0,this.screenY=d.screenY||
0):(this.clientX=void 0!==a.clientX?a.clientX:a.pageX,this.clientY=void 0!==a.clientY?a.clientY:a.pageY,this.screenX=a.screenX||0,this.screenY=a.screenY||0);this.button=a.button;this.keyCode=a.keyCode||0;this.key=a.key||"";this.charCode=a.charCode||("keypress"==c?a.keyCode:0);this.ctrlKey=a.ctrlKey;this.altKey=a.altKey;this.shiftKey=a.shiftKey;this.metaKey=a.metaKey;this.pointerId=a.pointerId||0;this.pointerType="string"===typeof a.pointerType?a.pointerType:Ud[a.pointerType]||"";this.state=a.state;
this.i=a;a.defaultPrevented&&Td.Z.preventDefault.call(this)};
Td.prototype.stopPropagation=function(){Td.Z.stopPropagation.call(this);this.i.stopPropagation?this.i.stopPropagation():this.i.cancelBubble=!0};
Td.prototype.preventDefault=function(){Td.Z.preventDefault.call(this);var a=this.i;a.preventDefault?a.preventDefault():a.returnValue=!1};var Vd="closure_listenable_"+(1E6*Math.random()|0);var Wd=0;function Xd(a,b,c,d,e){this.listener=a;this.proxy=null;this.src=b;this.type=c;this.capture=!!d;this.Na=e;this.key=++Wd;this.Aa=this.Ja=!1}
function Yd(a){a.Aa=!0;a.listener=null;a.proxy=null;a.src=null;a.Na=null}
;function Zd(a){this.src=a;this.listeners={};this.h=0}
Zd.prototype.add=function(a,b,c,d,e){var f=a.toString();a=this.listeners[f];a||(a=this.listeners[f]=[],this.h++);var g=$d(a,b,d,e);-1<g?(b=a[g],c||(b.Ja=!1)):(b=new Xd(b,this.src,f,!!d,e),b.Ja=c,a.push(b));return b};
Zd.prototype.remove=function(a,b,c,d){a=a.toString();if(!(a in this.listeners))return!1;var e=this.listeners[a];b=$d(e,b,c,d);return-1<b?(Yd(e[b]),Array.prototype.splice.call(e,b,1),0==e.length&&(delete this.listeners[a],this.h--),!0):!1};
function ae(a,b){var c=b.type;c in a.listeners&&ib(a.listeners[c],b)&&(Yd(b),0==a.listeners[c].length&&(delete a.listeners[c],a.h--))}
function $d(a,b,c,d){for(var e=0;e<a.length;++e){var f=a[e];if(!f.Aa&&f.listener==b&&f.capture==!!c&&f.Na==d)return e}return-1}
;var be="closure_lm_"+(1E6*Math.random()|0),ce={},de=0;function ee(a,b,c,d,e){if(d&&d.once)fe(a,b,c,d,e);else if(Array.isArray(b))for(var f=0;f<b.length;f++)ee(a,b[f],c,d,e);else c=ge(c),a&&a[Vd]?a.ba(b,c,Ma(d)?!!d.capture:!!d,e):he(a,b,c,!1,d,e)}
function he(a,b,c,d,e,f){if(!b)throw Error("Invalid event type");var g=Ma(e)?!!e.capture:!!e,h=ie(a);h||(a[be]=h=new Zd(a));c=h.add(b,c,d,g,f);if(!c.proxy){d=je();c.proxy=d;d.src=a;d.listener=c;if(a.addEventListener)Sd||(e=g),void 0===e&&(e=!1),a.addEventListener(b.toString(),d,e);else if(a.attachEvent)a.attachEvent(ke(b.toString()),d);else if(a.addListener&&a.removeListener)a.addListener(d);else throw Error("addEventListener and attachEvent are unavailable.");de++}}
function je(){function a(c){return b.call(a.src,a.listener,c)}
var b=le;return a}
function fe(a,b,c,d,e){if(Array.isArray(b))for(var f=0;f<b.length;f++)fe(a,b[f],c,d,e);else c=ge(c),a&&a[Vd]?a.i.add(String(b),c,!0,Ma(d)?!!d.capture:!!d,e):he(a,b,c,!0,d,e)}
function me(a,b,c,d,e){if(Array.isArray(b))for(var f=0;f<b.length;f++)me(a,b[f],c,d,e);else(d=Ma(d)?!!d.capture:!!d,c=ge(c),a&&a[Vd])?a.i.remove(String(b),c,d,e):a&&(a=ie(a))&&(b=a.listeners[b.toString()],a=-1,b&&(a=$d(b,c,d,e)),(c=-1<a?b[a]:null)&&ne(c))}
function ne(a){if("number"!==typeof a&&a&&!a.Aa){var b=a.src;if(b&&b[Vd])ae(b.i,a);else{var c=a.type,d=a.proxy;b.removeEventListener?b.removeEventListener(c,d,a.capture):b.detachEvent?b.detachEvent(ke(c),d):b.addListener&&b.removeListener&&b.removeListener(d);de--;(c=ie(b))?(ae(c,a),0==c.h&&(c.src=null,b[be]=null)):Yd(a)}}}
function ke(a){return a in ce?ce[a]:ce[a]="on"+a}
function le(a,b){if(a.Aa)a=!0;else{b=new Td(b,this);var c=a.listener,d=a.Na||a.src;a.Ja&&ne(a);a=c.call(d,b)}return a}
function ie(a){a=a[be];return a instanceof Zd?a:null}
var oe="__closure_events_fn_"+(1E9*Math.random()>>>0);function ge(a){if("function"===typeof a)return a;a[oe]||(a[oe]=function(b){return a.handleEvent(b)});
return a[oe]}
;function pe(){I.call(this);this.i=new Zd(this);this.Y=this;this.K=null}
Wa(pe,I);pe.prototype[Vd]=!0;pe.prototype.addEventListener=function(a,b,c,d){ee(this,a,b,c,d)};
pe.prototype.removeEventListener=function(a,b,c,d){me(this,a,b,c,d)};
function qe(a,b){var c=a.K;if(c){var d=[];for(var e=1;c;c=c.K)d.push(c),++e}a=a.Y;c=b.type||b;"string"===typeof b?b=new Nd(b,a):b instanceof Nd?b.target=b.target||a:(e=b,b=new Nd(c,a),ub(b,e));e=!0;if(d)for(var f=d.length-1;!b.j&&0<=f;f--){var g=b.h=d[f];e=re(g,c,!0,b)&&e}b.j||(g=b.h=a,e=re(g,c,!0,b)&&e,b.j||(e=re(g,c,!1,b)&&e));if(d)for(f=0;!b.j&&f<d.length;f++)g=b.h=d[f],e=re(g,c,!1,b)&&e}
pe.prototype.H=function(){pe.Z.H.call(this);if(this.i){var a=this.i,b=0,c;for(c in a.listeners){for(var d=a.listeners[c],e=0;e<d.length;e++)++b,Yd(d[e]);delete a.listeners[c];a.h--}}this.K=null};
pe.prototype.ba=function(a,b,c,d){return this.i.add(String(a),b,!1,c,d)};
function re(a,b,c,d){b=a.i.listeners[String(b)];if(!b)return!0;b=b.concat();for(var e=!0,f=0;f<b.length;++f){var g=b[f];if(g&&!g.Aa&&g.capture==c){var h=g.listener,k=g.Na||g.src;g.Ja&&ae(a.i,g);e=!1!==h.call(k,d)&&e}}return e&&!d.defaultPrevented}
;function se(a){var b,c;pe.call(this);var d=this;this.A=this.l=0;this.V=null!==a&&void 0!==a?a:{M:function(e,f){return setTimeout(e,f)},
U:clearTimeout};this.j=null!==(c=null===(b=window.navigator)||void 0===b?void 0:b.onLine)&&void 0!==c?c:!0;this.m=function(){return w(function(e){return v(e,te(d),0)})};
window.addEventListener("offline",this.m);window.addEventListener("online",this.m);this.A||ue(this)}
r(se,pe);se.prototype.dispose=function(){window.removeEventListener("offline",this.m);window.removeEventListener("online",this.m);this.V.U(this.A);delete se.h};
se.prototype.G=function(){return this.j};
function ue(a){a.A=a.V.M(function(){var b;return w(function(c){if(1==c.h)return a.j?(null===(b=window.navigator)||void 0===b?0:b.onLine)?c.s(3):v(c,te(a),3):v(c,te(a),3);ue(a);c.h=0})},3E4)}
function te(a,b){return a.u?a.u:a.u=new Promise(function(c){var d,e,f;return w(function(g){switch(g.h){case 1:return d=window.AbortController?new window.AbortController:void 0,e=null===d||void 0===d?void 0:d.signal,f=!1,sa(g,2,3),d&&(a.l=a.V.M(function(){d.abort()},b||2E4)),v(g,fetch("/generate_204",{method:"HEAD",
signal:e}),5);case 5:f=!0;case 3:va(g);a.u=void 0;a.l&&(a.V.U(a.l),a.l=0);f!==a.j&&(a.j=f,a.j?qe(a,"networkstatus-online"):qe(a,"networkstatus-offline"));c(f);wa(g);break;case 2:ua(g),f=!1,g.s(3)}})})}
;var ve={Vn:"WEB_DISPLAY_MODE_UNKNOWN",Rn:"WEB_DISPLAY_MODE_BROWSER",Tn:"WEB_DISPLAY_MODE_MINIMAL_UI",Un:"WEB_DISPLAY_MODE_STANDALONE",Sn:"WEB_DISPLAY_MODE_FULLSCREEN"};function we(){this.data_=[];this.h=-1}
we.prototype.set=function(a,b){b=void 0===b?!0:b;0<=a&&52>a&&0===a%1&&this.data_[a]!=b&&(this.data_[a]=b,this.h=-1)};
we.prototype.get=function(a){return!!this.data_[a]};
function xe(a){-1==a.h&&(a.h=fb(a.data_,function(b,c,d){return c?b+Math.pow(2,d):b},0));
return a.h}
;function ye(){var a={};this.C=function(b,c){return null!=a[b]?a[b]:c}}
;function ze(a,b){this.j=a;this.l=b;this.i=0;this.h=null}
ze.prototype.get=function(){if(0<this.i){this.i--;var a=this.h;this.h=a.next;a.next=null}else a=this.j();return a};
function Ae(a,b){a.l(b);100>a.i&&(a.i++,b.next=a.h,a.h=b)}
;var Be;function Ce(){var a=y.MessageChannel;"undefined"===typeof a&&"undefined"!==typeof window&&window.postMessage&&window.addEventListener&&!E("Presto")&&(a=function(){var e=jd("IFRAME");e.style.display="none";document.documentElement.appendChild(e);var f=e.contentWindow;e=f.document;e.open();e.close();var g="callImmediate"+Math.random(),h="file:"==f.location.protocol?"*":f.location.protocol+"//"+f.location.host;e=Ta(function(k){if(("*"==h||k.origin==h)&&k.data==g)this.port1.onmessage()},this);
f.addEventListener("message",e,!1);this.port1={};this.port2={postMessage:function(){f.postMessage(g,h)}}});
if("undefined"!==typeof a&&!E("Trident")&&!E("MSIE")){var b=new a,c={},d=c;b.port1.onmessage=function(){if(void 0!==c.next){c=c.next;var e=c.ob;c.ob=null;e()}};
return function(e){d.next={ob:e};d=d.next;b.port2.postMessage(0)}}return function(e){y.setTimeout(e,0)}}
;function De(a){y.setTimeout(function(){throw a;},0)}
;function Ee(){this.i=this.h=null}
Ee.prototype.add=function(a,b){var c=Fe.get();c.set(a,b);this.i?this.i.next=c:this.h=c;this.i=c};
Ee.prototype.remove=function(){var a=null;this.h&&(a=this.h,this.h=this.h.next,this.h||(this.i=null),a.next=null);return a};
var Fe=new ze(function(){return new Ge},function(a){return a.reset()});
function Ge(){this.next=this.scope=this.h=null}
Ge.prototype.set=function(a,b){this.h=a;this.scope=b;this.next=null};
Ge.prototype.reset=function(){this.next=this.scope=this.h=null};function He(a,b){Ie||Je();Ke||(Ie(),Ke=!0);Le.add(a,b)}
var Ie;function Je(){if(y.Promise&&y.Promise.resolve){var a=y.Promise.resolve(void 0);Ie=function(){a.then(Me)}}else Ie=function(){var b=Me;
"function"!==typeof y.setImmediate||y.Window&&y.Window.prototype&&!E("Edge")&&y.Window.prototype.setImmediate==y.setImmediate?(Be||(Be=Ce()),Be(b)):y.setImmediate(b)}}
var Ke=!1,Le=new Ee;function Me(){for(var a;a=Le.remove();){try{a.h.call(a.scope)}catch(b){De(b)}Ae(Fe,a)}Ke=!1}
;function Ne(a,b){this.h=a[y.Symbol.iterator]();this.i=b;this.j=0}
Ne.prototype[Symbol.iterator]=function(){return this};
Ne.prototype.next=function(){var a=this.h.next();return{value:a.done?void 0:this.i.call(void 0,a.value,this.j++),done:a.done}};
function Oe(a,b){return new Ne(a,b)}
;function Pe(){this.blockSize=-1}
;function Qe(){this.blockSize=-1;this.blockSize=64;this.h=[];this.o=[];this.m=[];this.j=[];this.j[0]=128;for(var a=1;a<this.blockSize;++a)this.j[a]=0;this.l=this.i=0;this.reset()}
Wa(Qe,Pe);Qe.prototype.reset=function(){this.h[0]=1732584193;this.h[1]=4023233417;this.h[2]=2562383102;this.h[3]=271733878;this.h[4]=3285377520;this.l=this.i=0};
function Re(a,b,c){c||(c=0);var d=a.m;if("string"===typeof b)for(var e=0;16>e;e++)d[e]=b.charCodeAt(c)<<24|b.charCodeAt(c+1)<<16|b.charCodeAt(c+2)<<8|b.charCodeAt(c+3),c+=4;else for(e=0;16>e;e++)d[e]=b[c]<<24|b[c+1]<<16|b[c+2]<<8|b[c+3],c+=4;for(e=16;80>e;e++){var f=d[e-3]^d[e-8]^d[e-14]^d[e-16];d[e]=(f<<1|f>>>31)&4294967295}b=a.h[0];c=a.h[1];var g=a.h[2],h=a.h[3],k=a.h[4];for(e=0;80>e;e++){if(40>e)if(20>e){f=h^c&(g^h);var m=1518500249}else f=c^g^h,m=1859775393;else 60>e?(f=c&g|h&(c|g),m=2400959708):
(f=c^g^h,m=3395469782);f=(b<<5|b>>>27)+f+k+m+d[e]&4294967295;k=h;h=g;g=(c<<30|c>>>2)&4294967295;c=b;b=f}a.h[0]=a.h[0]+b&4294967295;a.h[1]=a.h[1]+c&4294967295;a.h[2]=a.h[2]+g&4294967295;a.h[3]=a.h[3]+h&4294967295;a.h[4]=a.h[4]+k&4294967295}
Qe.prototype.update=function(a,b){if(null!=a){void 0===b&&(b=a.length);for(var c=b-this.blockSize,d=0,e=this.o,f=this.i;d<b;){if(0==f)for(;d<=c;)Re(this,a,d),d+=this.blockSize;if("string"===typeof a)for(;d<b;){if(e[f]=a.charCodeAt(d),++f,++d,f==this.blockSize){Re(this,e);f=0;break}}else for(;d<b;)if(e[f]=a[d],++f,++d,f==this.blockSize){Re(this,e);f=0;break}}this.i=f;this.l+=b}};
Qe.prototype.digest=function(){var a=[],b=8*this.l;56>this.i?this.update(this.j,56-this.i):this.update(this.j,this.blockSize-(this.i-56));for(var c=this.blockSize-1;56<=c;c--)this.o[c]=b&255,b/=256;Re(this,this.o);for(c=b=0;5>c;c++)for(var d=24;0<=d;d-=8)a[b]=this.h[c]>>d&255,++b;return a};function Se(a){return"string"==typeof a.className?a.className:a.getAttribute&&a.getAttribute("class")||""}
function Te(a,b){"string"==typeof a.className?a.className=b:a.setAttribute&&a.setAttribute("class",b)}
function Ue(a,b){a.classList?b=a.classList.contains(b):(a=a.classList?a.classList:Se(a).match(/\S+/g)||[],b=0<=bb(a,b));return b}
function Ve(){var a=document.body;a.classList?a.classList.remove("inverted-hdpi"):Ue(a,"inverted-hdpi")&&Te(a,Array.prototype.filter.call(a.classList?a.classList:Se(a).match(/\S+/g)||[],function(b){return"inverted-hdpi"!=b}).join(" "))}
;var We="StopIteration"in y?y.StopIteration:{message:"StopIteration",stack:""};function Xe(){}
Xe.prototype.da=function(){throw We;};
Xe.prototype.next=function(){return Ye};
var Ye={done:!0,value:void 0};function Ze(a){return{value:a,done:!1}}
function $e(a){if(a.done)throw We;return a.value}
Xe.prototype.T=function(){return this};function af(a){if(a instanceof bf||a instanceof cf||a instanceof df)return a;if("function"==typeof a.da)return new bf(function(){return ef(a)});
if("function"==typeof a[Symbol.iterator])return new bf(function(){return a[Symbol.iterator]()});
if("function"==typeof a.T)return new bf(function(){return ef(a.T())});
throw Error("Not an iterator or iterable.");}
function ef(a){if(!(a instanceof Xe))return a;var b=!1;return{next:function(){for(var c;!b;)try{c=a.da();break}catch(d){if(d!==We)throw d;b=!0}return{value:c,done:b}}}}
function bf(a){this.h=a}
bf.prototype.T=function(){return new cf(this.h())};
bf.prototype[Symbol.iterator]=function(){return new df(this.h())};
bf.prototype.i=function(){return new df(this.h())};
function cf(a){this.h=a}
r(cf,Xe);cf.prototype.da=function(){var a=this.h.next();if(a.done)throw We;return a.value};
cf.prototype.next=function(){return this.h.next()};
cf.prototype[Symbol.iterator]=function(){return new df(this.h)};
cf.prototype.i=function(){return new df(this.h)};
function df(a){bf.call(this,function(){return a});
this.j=a}
r(df,bf);df.prototype.next=function(){return this.j.next()};function ff(a,b){this.i={};this.h=[];this.ja=this.size=0;var c=arguments.length;if(1<c){if(c%2)throw Error("Uneven number of arguments");for(var d=0;d<c;d+=2)this.set(arguments[d],arguments[d+1])}else if(a)if(a instanceof ff)for(c=a.ab(),d=0;d<c.length;d++)this.set(c[d],a.get(c[d]));else for(d in a)this.set(d,a[d])}
l=ff.prototype;l.ab=function(){gf(this);return this.h.concat()};
l.has=function(a){return hf(this.i,a)};
l.equals=function(a,b){if(this===a)return!0;if(this.size!=a.size)return!1;b=b||jf;gf(this);for(var c,d=0;c=this.h[d];d++)if(!b(this.get(c),a.get(c)))return!1;return!0};
function jf(a,b){return a===b}
l.isEmpty=function(){return 0==this.size};
l.clear=function(){this.i={};this.ja=this.size=this.h.length=0};
l.remove=function(a){return this.delete(a)};
l.delete=function(a){return hf(this.i,a)?(delete this.i[a],--this.size,this.ja++,this.h.length>2*this.size&&gf(this),!0):!1};
function gf(a){if(a.size!=a.h.length){for(var b=0,c=0;b<a.h.length;){var d=a.h[b];hf(a.i,d)&&(a.h[c++]=d);b++}a.h.length=c}if(a.size!=a.h.length){var e={};for(c=b=0;b<a.h.length;)d=a.h[b],hf(e,d)||(a.h[c++]=d,e[d]=1),b++;a.h.length=c}}
l.get=function(a,b){return hf(this.i,a)?this.i[a]:b};
l.set=function(a,b){hf(this.i,a)||(this.size+=1,this.h.push(a),this.ja++);this.i[a]=b};
l.forEach=function(a,b){for(var c=this.ab(),d=0;d<c.length;d++){var e=c[d],f=this.get(e);a.call(b,f,e,this)}};
l.clone=function(){return new ff(this)};
l.keys=function(){return af(this.T(!0)).i()};
l.values=function(){return af(this.T(!1)).i()};
l.entries=function(){var a=this;return Oe(this.keys(),function(b){return[b,a.get(b)]})};
l.T=function(a){gf(this);var b=0,c=this.ja,d=this,e=new Xe;e.next=function(){if(c!=d.ja)throw Error("The map has changed since the iterator was created");if(b>=d.h.length)return Ye;var g=d.h[b++];return Ze(a?g:d.i[g])};
var f=e.next;e.da=function(){return $e(f.call(e))};
return e};
function hf(a,b){return Object.prototype.hasOwnProperty.call(a,b)}
;function kf(a){lf();return Gb(a)}
var lf=Ia;function mf(a){var b=[];nf(new of,a,b);return b.join("")}
function of(){}
function nf(a,b,c){if(null==b)c.push("null");else{if("object"==typeof b){if(Array.isArray(b)){var d=b;b=d.length;c.push("[");for(var e="",f=0;f<b;f++)c.push(e),nf(a,d[f],c),e=",";c.push("]");return}if(b instanceof String||b instanceof Number||b instanceof Boolean)b=b.valueOf();else{c.push("{");e="";for(d in b)Object.prototype.hasOwnProperty.call(b,d)&&(f=b[d],"function"!=typeof f&&(c.push(e),pf(d,c),c.push(":"),nf(a,f,c),e=","));c.push("}");return}}switch(typeof b){case "string":pf(b,c);break;case "number":c.push(isFinite(b)&&
!isNaN(b)?String(b):"null");break;case "boolean":c.push(String(b));break;case "function":c.push("null");break;default:throw Error("Unknown type: "+typeof b);}}}
var qf={'"':'\\"',"\\":"\\\\","/":"\\/","\b":"\\b","\f":"\\f","\n":"\\n","\r":"\\r","\t":"\\t","\v":"\\u000b"},rf=/\uffff/.test("\uffff")?/[\\"\x00-\x1f\x7f-\uffff]/g:/[\\"\x00-\x1f\x7f-\xff]/g;function pf(a,b){b.push('"',a.replace(rf,function(c){var d=qf[c];d||(d="\\u"+(c.charCodeAt(0)|65536).toString(16).substr(1),qf[c]=d);return d}),'"')}
;function sf(a){if(!a)return!1;try{return!!a.$goog_Thenable}catch(b){return!1}}
;function tf(a){this.h=0;this.D=void 0;this.l=this.i=this.j=null;this.o=this.m=!1;if(a!=Ia)try{var b=this;a.call(void 0,function(c){uf(b,2,c)},function(c){uf(b,3,c)})}catch(c){uf(this,3,c)}}
function vf(){this.next=this.context=this.onRejected=this.i=this.h=null;this.j=!1}
vf.prototype.reset=function(){this.context=this.onRejected=this.i=this.h=null;this.j=!1};
var wf=new ze(function(){return new vf},function(a){a.reset()});
function xf(a,b,c){var d=wf.get();d.i=a;d.onRejected=b;d.context=c;return d}
function yf(a){return new tf(function(b,c){c(a)})}
tf.prototype.then=function(a,b,c){return zf(this,"function"===typeof a?a:null,"function"===typeof b?b:null,c)};
tf.prototype.$goog_Thenable=!0;function Af(a,b){return zf(a,null,b,void 0)}
tf.prototype.cancel=function(a){if(0==this.h){var b=new Bf(a);He(function(){Cf(this,b)},this)}};
function Cf(a,b){if(0==a.h)if(a.j){var c=a.j;if(c.i){for(var d=0,e=null,f=null,g=c.i;g&&(g.j||(d++,g.h==a&&(e=g),!(e&&1<d)));g=g.next)e||(f=g);e&&(0==c.h&&1==d?Cf(c,b):(f?(d=f,d.next==c.l&&(c.l=d),d.next=d.next.next):Df(c),Ef(c,e,3,b)))}a.j=null}else uf(a,3,b)}
function Ff(a,b){a.i||2!=a.h&&3!=a.h||Gf(a);a.l?a.l.next=b:a.i=b;a.l=b}
function zf(a,b,c,d){var e=xf(null,null,null);e.h=new tf(function(f,g){e.i=b?function(h){try{var k=b.call(d,h);f(k)}catch(m){g(m)}}:f;
e.onRejected=c?function(h){try{var k=c.call(d,h);void 0===k&&h instanceof Bf?g(h):f(k)}catch(m){g(m)}}:g});
e.h.j=a;Ff(a,e);return e.h}
tf.prototype.A=function(a){this.h=0;uf(this,2,a)};
tf.prototype.K=function(a){this.h=0;uf(this,3,a)};
function uf(a,b,c){if(0==a.h){a===c&&(b=3,c=new TypeError("Promise cannot resolve to itself"));a.h=1;a:{var d=c,e=a.A,f=a.K;if(d instanceof tf){Ff(d,xf(e||Ia,f||null,a));var g=!0}else if(sf(d))d.then(e,f,a),g=!0;else{if(Ma(d))try{var h=d.then;if("function"===typeof h){Hf(d,h,e,f,a);g=!0;break a}}catch(k){f.call(a,k);g=!0;break a}g=!1}}g||(a.D=c,a.h=b,a.j=null,Gf(a),3!=b||c instanceof Bf||If(a,c))}}
function Hf(a,b,c,d,e){function f(k){h||(h=!0,d.call(e,k))}
function g(k){h||(h=!0,c.call(e,k))}
var h=!1;try{b.call(a,g,f)}catch(k){f(k)}}
function Gf(a){a.m||(a.m=!0,He(a.u,a))}
function Df(a){var b=null;a.i&&(b=a.i,a.i=b.next,b.next=null);a.i||(a.l=null);return b}
tf.prototype.u=function(){for(var a;a=Df(this);)Ef(this,a,this.h,this.D);this.m=!1};
function Ef(a,b,c,d){if(3==c&&b.onRejected&&!b.j)for(;a&&a.o;a=a.j)a.o=!1;if(b.h)b.h.j=null,Jf(b,c,d);else try{b.j?b.i.call(b.context):Jf(b,c,d)}catch(e){Kf.call(null,e)}Ae(wf,b)}
function Jf(a,b,c){2==b?a.i.call(a.context,c):a.onRejected&&a.onRejected.call(a.context,c)}
function If(a,b){a.o=!0;He(function(){a.o&&Kf.call(null,b)})}
var Kf=De;function Bf(a){Za.call(this,a)}
Wa(Bf,Za);Bf.prototype.name="cancel";function J(a){I.call(this);this.u=1;this.l=[];this.m=0;this.i=[];this.j={};this.A=!!a}
Wa(J,I);l=J.prototype;l.subscribe=function(a,b,c){var d=this.j[a];d||(d=this.j[a]=[]);var e=this.u;this.i[e]=a;this.i[e+1]=b;this.i[e+2]=c;this.u=e+3;d.push(e);return e};
function Lf(a,b,c,d){if(b=a.j[b]){var e=a.i;(b=b.find(function(f){return e[f+1]==c&&e[f+2]==d}))&&a.wa(b)}}
l.wa=function(a){var b=this.i[a];if(b){var c=this.j[b];0!=this.m?(this.l.push(a),this.i[a+1]=Ia):(c&&ib(c,a),delete this.i[a],delete this.i[a+1],delete this.i[a+2])}return!!b};
l.ka=function(a,b){var c=this.j[a];if(c){for(var d=Array(arguments.length-1),e=1,f=arguments.length;e<f;e++)d[e-1]=arguments[e];if(this.A)for(e=0;e<c.length;e++){var g=c[e];Mf(this.i[g+1],this.i[g+2],d)}else{this.m++;try{for(e=0,f=c.length;e<f&&!this.h();e++)g=c[e],this.i[g+1].apply(this.i[g+2],d)}finally{if(this.m--,0<this.l.length&&0==this.m)for(;c=this.l.pop();)this.wa(c)}}return 0!=e}return!1};
function Mf(a,b,c){He(function(){a.apply(b,c)})}
l.clear=function(a){if(a){var b=this.j[a];b&&(b.forEach(this.wa,this),delete this.j[a])}else this.i.length=0,this.j={}};
l.H=function(){J.Z.H.call(this);this.clear();this.l.length=0};function Nf(a){this.h=a}
Nf.prototype.set=function(a,b){void 0===b?this.h.remove(a):this.h.set(a,mf(b))};
Nf.prototype.get=function(a){try{var b=this.h.get(a)}catch(c){return}if(null!==b)try{return JSON.parse(b)}catch(c){throw"Storage: Invalid value was encountered";}};
Nf.prototype.remove=function(a){this.h.remove(a)};function Of(a){this.h=a}
Wa(Of,Nf);function Pf(a){this.data=a}
function Qf(a){return void 0===a||a instanceof Pf?a:new Pf(a)}
Of.prototype.set=function(a,b){Of.Z.set.call(this,a,Qf(b))};
Of.prototype.i=function(a){a=Of.Z.get.call(this,a);if(void 0===a||a instanceof Object)return a;throw"Storage: Invalid value was encountered";};
Of.prototype.get=function(a){if(a=this.i(a)){if(a=a.data,void 0===a)throw"Storage: Invalid value was encountered";}else a=void 0;return a};function Rf(a){this.h=a}
Wa(Rf,Of);Rf.prototype.set=function(a,b,c){if(b=Qf(b)){if(c){if(c<Date.now()){Rf.prototype.remove.call(this,a);return}b.expiration=c}b.creation=Date.now()}Rf.Z.set.call(this,a,b)};
Rf.prototype.i=function(a){var b=Rf.Z.i.call(this,a);if(b){var c=b.creation,d=b.expiration;if(d&&d<Date.now()||c&&c>Date.now())Rf.prototype.remove.call(this,a);else return b}};function Sf(){}
;function Tf(){}
Wa(Tf,Sf);Tf.prototype[Symbol.iterator]=function(){return af(this.T(!0)).i()};
Tf.prototype.clear=function(){var a=Array.from(this);a=q(a);for(var b=a.next();!b.done;b=a.next())this.remove(b.value)};function Uf(a){this.h=a}
Wa(Uf,Tf);l=Uf.prototype;l.isAvailable=function(){if(!this.h)return!1;try{return this.h.setItem("__sak","1"),this.h.removeItem("__sak"),!0}catch(a){return!1}};
l.set=function(a,b){try{this.h.setItem(a,b)}catch(c){if(0==this.h.length)throw"Storage mechanism: Storage disabled";throw"Storage mechanism: Quota exceeded";}};
l.get=function(a){a=this.h.getItem(a);if("string"!==typeof a&&null!==a)throw"Storage mechanism: Invalid value was encountered";return a};
l.remove=function(a){this.h.removeItem(a)};
l.T=function(a){var b=0,c=this.h,d=new Xe;d.next=function(){if(b>=c.length)return Ye;var f=c.key(b++);if(a)return Ze(f);f=c.getItem(f);if("string"!==typeof f)throw"Storage mechanism: Invalid value was encountered";return Ze(f)};
var e=d.next;d.da=function(){return $e(e.call(d))};
return d};
l.clear=function(){this.h.clear()};
l.key=function(a){return this.h.key(a)};function Vf(){var a=null;try{a=window.localStorage||null}catch(b){}this.h=a}
Wa(Vf,Uf);function Wf(a,b){this.i=a;this.h=null;var c;if(c=gc)c=!(9<=Number(tc));if(c){Xf||(Xf=new ff);this.h=Xf.get(a);this.h||(b?this.h=document.getElementById(b):(this.h=document.createElement("userdata"),this.h.addBehavior("#default#userData"),document.body.appendChild(this.h)),Xf.set(a,this.h));try{this.h.load(this.i)}catch(d){this.h=null}}}
Wa(Wf,Tf);var Yf={".":".2E","!":".21","~":".7E","*":".2A","'":".27","(":".28",")":".29","%":"."},Xf=null;function Zf(a){return"_"+encodeURIComponent(a).replace(/[.!~*'()%]/g,function(b){return Yf[b]})}
l=Wf.prototype;l.isAvailable=function(){return!!this.h};
l.set=function(a,b){this.h.setAttribute(Zf(a),b);$f(this)};
l.get=function(a){a=this.h.getAttribute(Zf(a));if("string"!==typeof a&&null!==a)throw"Storage mechanism: Invalid value was encountered";return a};
l.remove=function(a){this.h.removeAttribute(Zf(a));$f(this)};
l.T=function(a){var b=0,c=this.h.XMLDocument.documentElement.attributes,d=new Xe;d.next=function(){if(b>=c.length)return Ye;var f=c[b++];if(a)return Ze(decodeURIComponent(f.nodeName.replace(/\./g,"%")).substr(1));f=f.nodeValue;if("string"!==typeof f)throw"Storage mechanism: Invalid value was encountered";return Ze(f)};
var e=d.next;d.da=function(){return $e(e.call(d))};
return d};
l.clear=function(){for(var a=this.h.XMLDocument.documentElement,b=a.attributes.length;0<b;b--)a.removeAttribute(a.attributes[b-1].nodeName);$f(this)};
function $f(a){try{a.h.save(a.i)}catch(b){throw"Storage mechanism: Quota exceeded";}}
;function ag(a,b){this.i=a;this.h=b+"::"}
Wa(ag,Tf);ag.prototype.set=function(a,b){this.i.set(this.h+a,b)};
ag.prototype.get=function(a){return this.i.get(this.h+a)};
ag.prototype.remove=function(a){this.i.remove(this.h+a)};
ag.prototype.T=function(a){var b=this.i.T(!0),c=this,d=new Xe;d.next=function(){try{var f=b.da()}catch(g){if(g===We)return Ye;throw g;}for(;f.substr(0,c.h.length)!=c.h;)try{f=b.da()}catch(g){if(g===We)return Ye;throw g;}return Ze(a?f.substr(c.h.length):c.i.get(f))};
var e=d.next;d.da=function(){return $e(e.call(d))};
return d};function bg(a){F.call(this,a)}
r(bg,F);bg.prototype.getKey=function(){return Mc(this,1)};
bg.prototype.getValue=function(){return Mc(this,2===Pc(this,cg)?2:-1)};
bg.prototype.setValue=function(a){return Oc(this,2,cg,a)};
var cg=[2,3,4,5,6];function dg(a){F.call(this,a)}
r(dg,F);function eg(a){F.call(this,a)}
r(eg,F);function fg(a){F.call(this,a)}
r(fg,F);function gg(a){F.call(this,a,-1,hg)}
r(gg,F);gg.prototype.getPlayerType=function(){return Mc(this,36)};
gg.prototype.setHomeGroupInfo=function(a){return H(this,81,a)};
var hg=[9,66,24,32,86,100,101];function ig(a){F.call(this,a,-1,jg)}
r(ig,F);var jg=[15,26,28];function kg(a){F.call(this,a)}
r(kg,F);kg.prototype.setToken=function(a){return G(this,2,a)};function lg(a){F.call(this,a,-1,mg)}
r(lg,F);lg.prototype.setSafetyMode=function(a){return G(this,5,a)};
var mg=[12];function ng(a){F.call(this,a,-1,og)}
r(ng,F);var og=[12];function pg(a){F.call(this,a)}
r(pg,F);var qg={rh:0,bh:1,ih:2,jh:4,oh:8,kh:16,lh:32,qh:64,ph:128,eh:256,gh:512,nh:1024,fh:2048,hh:4096,dh:8192,mh:16384};function rg(a){F.call(this,a)}
r(rg,F);function sg(a,b){H(a,1,b)}
rg.prototype.X=function(a){G(this,2,a)};
function tg(a){F.call(this,a)}
r(tg,F);function ug(a,b){H(a,1,b)}
;function vg(a){F.call(this,a,-1,wg)}
r(vg,F);vg.prototype.X=function(a){G(this,1,a)};
function xg(a,b){H(a,2,b)}
var wg=[3];function yg(a){F.call(this,a)}
r(yg,F);yg.prototype.X=function(a){G(this,1,a)};function zg(a){F.call(this,a)}
r(zg,F);zg.prototype.X=function(a){G(this,1,a)};function Ag(a){F.call(this,a)}
r(Ag,F);Ag.prototype.X=function(a){G(this,1,a)};function Bg(a){F.call(this,a)}
r(Bg,F);function Cg(a){F.call(this,a)}
r(Cg,F);function Dg(a){F.call(this,a,-1,Eg)}
r(Dg,F);Dg.prototype.getPlayerType=function(){var a=Mc(this,7);return null==a?0:a};
Dg.prototype.setVideoId=function(a){return G(this,19,a)};
function Fg(a){F.call(this,a)}
r(Fg,F);Fg.prototype.getId=function(){var a=Mc(this,2);return null==a?"":a};
var Eg=[83,68];function Gg(a){F.call(this,a)}
r(Gg,F);function Hg(a){F.call(this,a)}
r(Hg,F);function Ig(a){F.call(this,a)}
r(Ig,F);function Jg(a){F.call(this,a,421)}
r(Jg,F);
var Tc=[23,24,11,6,7,5,2,3,20,21,28,32,37,229,241,45,59,225,288,72,73,78,208,156,202,215,74,76,79,80,111,85,91,97,100,102,105,119,126,127,136,146,157,158,159,163,164,168,176,222,383,177,178,179,411,184,188,189,190,191,193,194,195,196,198,199,200,201,203,204,205,206,258,259,260,261,209,226,227,232,233,234,240,247,248,251,254,255,270,278,291,293,300,304,308,309,310,311,313,314,319,321,323,324,328,330,331,332,337,338,340,344,348,350,351,352,353,354,355,356,357,358,361,363,364,368,369,370,373,374,375,
378,380,381,388,389,403,412,413,414,415,416,417,418,419,420,117];function Kg(a){F.call(this,a)}
r(Kg,F);function Lg(a){F.call(this,a)}
r(Lg,F);Lg.prototype.setVideoId=function(a){return Oc(this,1,Mg,a)};
Lg.prototype.getPlaylistId=function(){return Mc(this,2===Pc(this,Mg)?2:-1)};
var Mg=[1,2];function Ng(a){F.call(this,a,-1,Og)}
r(Ng,F);var Og=[3];function Pg(a){F.call(this,a,1)}
r(Pg,F);function Qg(a){F.call(this,a)}
r(Qg,F);var Rg;Rg=new function(a,b,c,d){this.h=a;this.fieldName=b;this.isRepeated=d;this.i=Xc}(406606992,{so:0},Qg,0);function Sg(){Qg.apply(this,arguments)}
r(Sg,Qg);function Tg(a,b){1<b.length?a[b[0]]=b[1]:1===b.length&&Object.assign(a,b[0])}
;var Ug,Vg,Wg,Xg=y.window,Yg=(null===(Ug=null===Xg||void 0===Xg?void 0:Xg.yt)||void 0===Ug?void 0:Ug.config_)||(null===(Vg=null===Xg||void 0===Xg?void 0:Xg.ytcfg)||void 0===Vg?void 0:Vg.data_)||{},Zg=(null===(Wg=null===Xg||void 0===Xg?void 0:Xg.ytcfg)||void 0===Wg?void 0:Wg.obfuscatedData_)||[];function $g(){Pg.apply(this,arguments)}
r($g,Pg);var ah=new $g(Zg),bh=Yg.EXPERIMENT_FLAGS;if(!bh||!bh.jspb_i18n_extension){var ch=new Sg;Rg.i(ah,ch)}z("yt.config_",Yg,void 0);z("yt.configJspb_",Zg,void 0);function dh(){Tg(Yg,arguments)}
function B(a,b){return a in Yg?Yg[a]:b}
function eh(a){return B(a,void 0)}
;function L(a){a=fh(a);return"string"===typeof a&&"false"===a?!1:!!a}
function gh(a,b){a=fh(a);return void 0===a&&void 0!==b?b:Number(a||0)}
function hh(){return B("EXPERIMENTS_TOKEN","")}
function fh(a){var b=B("EXPERIMENTS_FORCED_FLAGS",{});return void 0!==b[a]?b[a]:B("EXPERIMENT_FLAGS",{})[a]}
function ih(){var a=[],b=B("EXPERIMENTS_FORCED_FLAGS",{});for(c in b)a.push({key:c,value:String(b[c])});var c=B("EXPERIMENT_FLAGS",{});for(var d in c)d.startsWith("force_")&&void 0===b[d]&&a.push({key:d,value:String(c[d])});return a}
;var jh={appSettingsCaptured:!0,visualElementAttached:!0,visualElementGestured:!0,visualElementHidden:!0,visualElementShown:!0,flowEvent:!0,visualElementStateChanged:!0,playbackAssociated:!0,youThere:!0,accountStateChangeSignedIn:!0,accountStateChangeSignedOut:!0},kh={latencyActionBaselined:!0,latencyActionInfo:!0,latencyActionTicked:!0,bedrockRepetitiveActionTimed:!0,adsClientStateChange:!0,streamzIncremented:!0,mdxDialAdditionalDataUpdateEvent:!0,tvhtml5WatchKeyEvent:!0,tvhtml5VideoSeek:!0,tokenRefreshEvent:!0,
adNotify:!0,adNotifyFilled:!0,tvhtml5LaunchUrlComponentChanged:!0,bedrockResourceConsumptionSnapshot:!0,deviceStartupMetrics:!0,mdxSignIn:!0,tvhtml5KeyboardLogging:!0,tvhtml5StartupSoundEvent:!0,tvhtml5LiveChatStatus:!0,tvhtml5DeviceStorageStatus:!0,tvhtml5LocalStorage:!0,directSignInEvent:!0,finalPayload:!0,tvhtml5SearchCompleted:!0,tvhtml5KeyboardPerformance:!0,adNotifyFailure:!0,latencyActionSpan:!0,tvhtml5AccountDialogOpened:!0,tvhtml5ApiTest:!0};var lh=0,mh=jc?"webkit":ic?"moz":gc?"ms":fc?"o":"";z("ytDomDomGetNextId",A("ytDomDomGetNextId")||function(){return++lh},void 0);var nh=[];function oh(a){nh.forEach(function(b){return b(a)})}
function ph(a){return a&&window.yterr?function(){try{return a.apply(this,arguments)}catch(b){qh(b)}}:a}
function qh(a,b,c,d){var e=A("yt.logging.errors.log");e?e(a,"ERROR",b,c,d):(e=B("ERRORS",[]),e.push([a,"ERROR",b,c,d]),dh("ERRORS",e));oh(a)}
function rh(a,b,c,d){var e=A("yt.logging.errors.log");e?e(a,"WARNING",b,c,d):(e=B("ERRORS",[]),e.push([a,"WARNING",b,c,d]),dh("ERRORS",e))}
;var sh={stopImmediatePropagation:1,stopPropagation:1,preventMouseEvent:1,preventManipulation:1,preventDefault:1,layerX:1,layerY:1,screenX:1,screenY:1,scale:1,rotation:1,webkitMovementX:1,webkitMovementY:1};
function th(a){this.type="";this.state=this.source=this.data=this.currentTarget=this.relatedTarget=this.target=null;this.charCode=this.keyCode=0;this.metaKey=this.shiftKey=this.ctrlKey=this.altKey=!1;this.rotation=this.clientY=this.clientX=0;this.scale=1;this.changedTouches=this.touches=null;try{if(a=a||window.event){this.event=a;for(var b in a)b in sh||(this[b]=a[b]);this.scale=a.scale;this.rotation=a.rotation;var c=a.target||a.srcElement;c&&3==c.nodeType&&(c=c.parentNode);this.target=c;var d=a.relatedTarget;
if(d)try{d=d.nodeName?d:null}catch(e){d=null}else"mouseover"==this.type?d=a.fromElement:"mouseout"==this.type&&(d=a.toElement);this.relatedTarget=d;this.clientX=void 0!=a.clientX?a.clientX:a.pageX;this.clientY=void 0!=a.clientY?a.clientY:a.pageY;this.keyCode=a.keyCode?a.keyCode:a.which;this.charCode=a.charCode||("keypress"==this.type?this.keyCode:0);this.altKey=a.altKey;this.ctrlKey=a.ctrlKey;this.shiftKey=a.shiftKey;this.metaKey=a.metaKey;this.h=a.pageX;this.i=a.pageY}}catch(e){}}
function uh(a){if(document.body&&document.documentElement){var b=document.body.scrollTop+document.documentElement.scrollTop;a.h=a.clientX+(document.body.scrollLeft+document.documentElement.scrollLeft);a.i=a.clientY+b}}
th.prototype.preventDefault=function(){this.event&&(this.event.returnValue=!1,this.event.preventDefault&&this.event.preventDefault())};
th.prototype.stopPropagation=function(){this.event&&(this.event.cancelBubble=!0,this.event.stopPropagation&&this.event.stopPropagation())};
th.prototype.stopImmediatePropagation=function(){this.event&&(this.event.cancelBubble=!0,this.event.stopImmediatePropagation&&this.event.stopImmediatePropagation())};var mb=y.ytEventsEventsListeners||{};z("ytEventsEventsListeners",mb,void 0);var vh=y.ytEventsEventsCounter||{count:0};z("ytEventsEventsCounter",vh,void 0);
function wh(a,b,c,d){d=void 0===d?{}:d;a.addEventListener&&("mouseenter"!=b||"onmouseenter"in document?"mouseleave"!=b||"onmouseenter"in document?"mousewheel"==b&&"MozBoxSizing"in document.documentElement.style&&(b="MozMousePixelScroll"):b="mouseout":b="mouseover");return lb(function(e){var f="boolean"===typeof e[4]&&e[4]==!!d,g=Ma(e[4])&&Ma(d)&&qb(e[4],d);return!!e.length&&e[0]==a&&e[1]==b&&e[2]==c&&(f||g)})}
var xh=ab(function(){var a=!1;try{var b=Object.defineProperty({},"capture",{get:function(){a=!0}});
window.addEventListener("test",null,b)}catch(c){}return a});
function yh(a,b,c,d){d=void 0===d?{}:d;if(!a||!a.addEventListener&&!a.attachEvent)return"";var e=wh(a,b,c,d);if(e)return e;e=++vh.count+"";var f=!("mouseenter"!=b&&"mouseleave"!=b||!a.addEventListener||"onmouseenter"in document);var g=f?function(h){h=new th(h);if(!kd(h.relatedTarget,function(k){return k==a}))return h.currentTarget=a,h.type=b,c.call(a,h)}:function(h){h=new th(h);
h.currentTarget=a;return c.call(a,h)};
g=ph(g);a.addEventListener?("mouseenter"==b&&f?b="mouseover":"mouseleave"==b&&f?b="mouseout":"mousewheel"==b&&"MozBoxSizing"in document.documentElement.style&&(b="MozMousePixelScroll"),xh()||"boolean"===typeof d?a.addEventListener(b,g,d):a.addEventListener(b,g,!!d.capture)):a.attachEvent("on"+b,g);mb[e]=[a,b,c,g,d];return e}
function zh(a){a&&("string"==typeof a&&(a=[a]),cb(a,function(b){if(b in mb){var c=mb[b],d=c[0],e=c[1],f=c[3];c=c[4];d.removeEventListener?xh()||"boolean"===typeof c?d.removeEventListener(e,f,c):d.removeEventListener(e,f,!!c.capture):d.detachEvent&&d.detachEvent("on"+e,f);delete mb[b]}}))}
;var Ah=window.ytcsi&&window.ytcsi.now?window.ytcsi.now:window.performance&&window.performance.timing&&window.performance.now&&window.performance.timing.navigationStart?function(){return window.performance.timing.navigationStart+window.performance.now()}:function(){return(new Date).getTime()};function Bh(a,b){"function"===typeof a&&(a=ph(a));return window.setTimeout(a,b)}
function Ch(a){window.clearTimeout(a)}
;function Dh(a){this.K=a;this.i=null;this.m=0;this.A=null;this.u=0;this.j=[];for(a=0;4>a;a++)this.j.push(0);this.l=0;this.S=yh(window,"mousemove",Ta(this.Y,this));a=Ta(this.L,this);"function"===typeof a&&(a=ph(a));this.ga=window.setInterval(a,25)}
Wa(Dh,I);Dh.prototype.Y=function(a){void 0===a.h&&uh(a);var b=a.h;void 0===a.i&&uh(a);this.i=new gd(b,a.i)};
Dh.prototype.L=function(){if(this.i){var a=Ah();if(0!=this.m){var b=this.A,c=this.i,d=b.x-c.x;b=b.y-c.y;d=Math.sqrt(d*d+b*b)/(a-this.m);this.j[this.l]=.5<Math.abs((d-this.u)/this.u)?1:0;for(c=b=0;4>c;c++)b+=this.j[c]||0;3<=b&&this.K();this.u=d}this.m=a;this.A=this.i;this.l=(this.l+1)%4}};
Dh.prototype.H=function(){window.clearInterval(this.ga);zh(this.S)};function Eh(){}
function Fh(a,b){return Gh(a,0,b)}
Eh.prototype.M=function(a,b){return Gh(a,1,b)};
function Hh(a,b){Gh(a,2,b)}
function Ih(a){var b=A("yt.scheduler.instance.addImmediateJob");b?b(a):a()}
;function Jh(){Eh.apply(this,arguments)}
r(Jh,Eh);function Kh(){Jh.h||(Jh.h=new Jh);return Jh.h}
function Gh(a,b,c){void 0!==c&&Number.isNaN(Number(c))&&(c=void 0);var d=A("yt.scheduler.instance.addJob");return d?d(a,b,c):void 0===c?(a(),NaN):Bh(a,c||0)}
Jh.prototype.U=function(a){if(void 0===a||!Number.isNaN(Number(a))){var b=A("yt.scheduler.instance.cancelJob");b?b(a):Ch(a)}};
Jh.prototype.start=function(){var a=A("yt.scheduler.instance.start");a&&a()};
Jh.prototype.pause=function(){var a=A("yt.scheduler.instance.pause");a&&a()};var Lh=Kh();var Mh={};
function Nh(a){var b=void 0===a?{}:a;a=void 0===b.fc?!1:b.fc;b=void 0===b.Ub?!0:b.Ub;if(null==A("_lact",window)){var c=parseInt(B("LACT"),10);c=isFinite(c)?Date.now()-Math.max(c,0):-1;z("_lact",c,window);z("_fact",c,window);-1==c&&Oh();yh(document,"keydown",Oh);yh(document,"keyup",Oh);yh(document,"mousedown",Oh);yh(document,"mouseup",Oh);a?yh(window,"touchmove",function(){Ph("touchmove",200)},{passive:!0}):(yh(window,"resize",function(){Ph("resize",200)}),b&&yh(window,"scroll",function(){Ph("scroll",200)}));
new Dh(function(){Ph("mouse",100)});
yh(document,"touchstart",Oh,{passive:!0});yh(document,"touchend",Oh,{passive:!0})}}
function Ph(a,b){Mh[a]||(Mh[a]=!0,Lh.M(function(){Oh();Mh[a]=!1},b))}
function Oh(){null==A("_lact",window)&&Nh();var a=Date.now();z("_lact",a,window);-1==A("_fact",window)&&z("_fact",a,window);(a=A("ytglobal.ytUtilActivityCallback_"))&&a()}
function Qh(){var a=A("_lact",window);return null==a?-1:Math.max(Date.now()-a,0)}
;function Rh(){var a=Sh;A("yt.ads.biscotti.getId_")||z("yt.ads.biscotti.getId_",a,void 0)}
function Th(a){z("yt.ads.biscotti.lastId_",a,void 0)}
;var Uh=/^[\w.]*$/,Vh={q:!0,search_query:!0};function Wh(a,b){b=a.split(b);for(var c={},d=0,e=b.length;d<e;d++){var f=b[d].split("=");if(1==f.length&&f[0]||2==f.length)try{var g=Xh(f[0]||""),h=Xh(f[1]||"");g in c?Array.isArray(c[g])?jb(c[g],h):c[g]=[c[g],h]:c[g]=h}catch(t){var k=t,m=f[0],n=String(Wh);k.args=[{key:m,value:f[1],query:a,method:Yh==n?"unchanged":n}];Vh.hasOwnProperty(m)||rh(k)}}return c}
var Yh=String(Wh);function Zh(a){var b=[];kb(a,function(c,d){var e=encodeURIComponent(String(d)),f;Array.isArray(c)?f=c:f=[c];cb(f,function(g){""==g?b.push(e):b.push(e+"="+encodeURIComponent(String(g)))})});
return b.join("&")}
function $h(a){"?"==a.charAt(0)&&(a=a.substr(1));return Wh(a,"&")}
function ai(a){return-1!=a.indexOf("?")?(a=(a||"").split("#")[0],a=a.split("?",2),$h(1<a.length?a[1]:a[0])):{}}
function bi(a,b,c){var d=a.split("#",2);a=d[0];d=1<d.length?"#"+d[1]:"";var e=a.split("?",2);a=e[0];e=$h(e[1]||"");for(var f in b)!c&&null!==e&&f in e||(e[f]=b[f]);return bc(a,e)+d}
function ci(a){if(!b)var b=window.location.href;var c=a.match(Wb)[1]||null,d=Yb(a);c&&d?(a=a.match(Wb),b=b.match(Wb),a=a[3]==b[3]&&a[1]==b[1]&&a[4]==b[4]):a=d?Yb(b)==d&&(Number(b.match(Wb)[4]||null)||null)==(Number(a.match(Wb)[4]||null)||null):!0;return a}
function Xh(a){return a&&a.match(Uh)?a:decodeURIComponent(a.replace(/\+/g," "))}
;function di(a){var b=ei;a=void 0===a?A("yt.ads.biscotti.lastId_")||"":a;var c=Object,d=c.assign,e={};e.dt=vd;e.flash="0";a:{try{var f=b.h.top.location.href}catch(hb){f=2;break a}f=f?f===b.i.location.href?0:1:2}e=(e.frm=f,e);try{e.u_tz=-(new Date).getTimezoneOffset();var g=void 0===g?$c:g;try{var h=g.history.length}catch(hb){h=0}e.u_his=h;var k;e.u_h=null==(k=$c.screen)?void 0:k.height;var m;e.u_w=null==(m=$c.screen)?void 0:m.width;var n;e.u_ah=null==(n=$c.screen)?void 0:n.availHeight;var t;e.u_aw=
null==(t=$c.screen)?void 0:t.availWidth;var x;e.u_cd=null==(x=$c.screen)?void 0:x.colorDepth}catch(hb){}h=b.h;try{var u=h.screenX;var C=h.screenY}catch(hb){}try{var D=h.outerWidth;var K=h.outerHeight}catch(hb){}try{var N=h.innerWidth;var S=h.innerHeight}catch(hb){}try{var W=h.screenLeft;var Qa=h.screenTop}catch(hb){}try{N=h.innerWidth,S=h.innerHeight}catch(hb){}try{var zb=h.screen.availWidth;var P=h.screen.availTop}catch(hb){}u=[W,Qa,u,C,zb,P,D,K,N,S];try{var Y=(b.h.top||window).document,fa="CSS1Compat"==
Y.compatMode?Y.documentElement:Y.body;var Ya=(new hd(fa.clientWidth,fa.clientHeight)).round()}catch(hb){Ya=new hd(-12245933,-12245933)}Y=Ya;Ya={};fa=new we;y.SVGElement&&y.document.createElementNS&&fa.set(0);C=pd();C["allow-top-navigation-by-user-activation"]&&fa.set(1);C["allow-popups-to-escape-sandbox"]&&fa.set(2);y.crypto&&y.crypto.subtle&&fa.set(3);y.TextDecoder&&y.TextEncoder&&fa.set(4);fa=xe(fa);Ya.bc=fa;Ya.bih=Y.height;Ya.biw=Y.width;Ya.brdim=u.join();b=b.i;Y="ma";ye.ma&&ye.hasOwnProperty(Y)?
Y=ye.ma:(fa=new ye,ye.ma=fa,ye.hasOwnProperty(Y),Y=fa);b=(Ya.vis=Y.C(wd.flag,wd.defaultValue)&&b.prerendering?3:{visible:1,hidden:2,prerender:3,preview:4,unloaded:5}[b.visibilityState||b.webkitVisibilityState||b.mozVisibilityState||""]||0,Ya.wgl=!!$c.WebGLRenderingContext,Ya);c=d.call(c,e,b);c.ca_type="image";a&&(c.bid=a);return c}
var ei=new function(){var a=window.document;this.h=window;this.i=a};
z("yt.ads_.signals_.getAdSignalsString",function(a){return Zh(di(a))},void 0);Date.now();var fi="XMLHttpRequest"in y?function(){return new XMLHttpRequest}:null;
function gi(){if(!fi)return null;var a=fi();return"open"in a?a:null}
function hi(a){switch(a&&"status"in a?a.status:-1){case 200:case 201:case 202:case 203:case 204:case 205:case 206:case 304:return!0;default:return!1}}
;var ii={Authorization:"AUTHORIZATION","X-Goog-Visitor-Id":"SANDBOXED_VISITOR_ID","X-Youtube-Domain-Admin-State":"DOMAIN_ADMIN_STATE","X-Youtube-Chrome-Connected":"CHROME_CONNECTED_HEADER","X-YouTube-Client-Name":"INNERTUBE_CONTEXT_CLIENT_NAME","X-YouTube-Client-Version":"INNERTUBE_CONTEXT_CLIENT_VERSION","X-YouTube-Delegation-Context":"INNERTUBE_CONTEXT_SERIALIZED_DELEGATION_CONTEXT","X-YouTube-Device":"DEVICE","X-Youtube-Identity-Token":"ID_TOKEN","X-YouTube-Page-CL":"PAGE_CL","X-YouTube-Page-Label":"PAGE_BUILD_LABEL",
"X-YouTube-Variants-Checksum":"VARIANTS_CHECKSUM"},ji="app debugcss debugjs expflag force_ad_params force_ad_encrypted force_viral_ad_response_params forced_experiments innertube_snapshots innertube_goldens internalcountrycode internalipoverride absolute_experiments conditional_experiments sbb sr_bns_address".split(" ").concat(ha("client_dev_mss_url client_dev_regex_map client_dev_root_url expflag jsfeat jsmode client_rollout_override".split(" "))),ki=!1;
function li(a,b){b=void 0===b?{}:b;var c=ci(a),d=L("web_ajax_ignore_global_headers_if_set"),e;for(e in ii){var f=B(ii[e]);!f||!c&&Yb(a)||d&&void 0!==b[e]||(b[e]=f)}if(c||!Yb(a))b["X-YouTube-Utc-Offset"]=String(-(new Date).getTimezoneOffset());if(c||!Yb(a)){try{var g=(new Intl.DateTimeFormat).resolvedOptions().timeZone}catch(h){}g&&(b["X-YouTube-Time-Zone"]=g)}if(c||!Yb(a))b["X-YouTube-Ad-Signals"]=Zh(di(void 0));return b}
function mi(a){var b=window.location.search,c=Yb(a);L("debug_handle_relative_url_for_query_forward_killswitch")||c||!ci(a)||(c=document.location.hostname);var d=Xb(a.match(Wb)[5]||null);d=(c=c&&(c.endsWith("youtube.com")||c.endsWith("youtube-nocookie.com")))&&d&&d.startsWith("/api/");if(!c||d)return a;var e=$h(b),f={};cb(ji,function(g){e[g]&&(f[g]=e[g])});
return bi(a,f||{},!1)}
function ni(a,b){var c=b.format||"JSON";a=oi(a,b);var d=pi(a,b),e=!1,f=qi(a,function(k){if(!e){e=!0;h&&Ch(h);var m=hi(k),n=null,t=400<=k.status&&500>k.status,x=500<=k.status&&600>k.status;if(m||t||x)n=ri(a,c,k,b.convertToSafeHtml);if(m)a:if(k&&204==k.status)m=!0;else{switch(c){case "XML":m=0==parseInt(n&&n.return_code,10);break a;case "RAW":m=!0;break a}m=!!n}n=n||{};t=b.context||y;m?b.onSuccess&&b.onSuccess.call(t,k,n):b.onError&&b.onError.call(t,k,n);b.onFinish&&b.onFinish.call(t,k,n)}},b.method,
d,b.headers,b.responseType,b.withCredentials);
if(b.onTimeout&&0<b.timeout){var g=b.onTimeout;var h=Bh(function(){e||(e=!0,f.abort(),Ch(h),g.call(b.context||y,f))},b.timeout)}return f}
function oi(a,b){b.includeDomain&&(a=document.location.protocol+"//"+document.location.hostname+(document.location.port?":"+document.location.port:"")+a);var c=B("XSRF_FIELD_NAME",void 0);if(b=b.urlParams)b[c]&&delete b[c],a=bi(a,b||{},!0);return a}
function pi(a,b){var c=B("XSRF_FIELD_NAME",void 0),d=B("XSRF_TOKEN",void 0),e=b.postBody||"",f=b.postParams,g=eh("XSRF_FIELD_NAME"),h;b.headers&&(h=b.headers["Content-Type"]);b.excludeXsrf||Yb(a)&&!b.withCredentials&&Yb(a)!=document.location.hostname||"POST"!=b.method||h&&"application/x-www-form-urlencoded"!=h||b.postParams&&b.postParams[g]||(f||(f={}),f[c]=d);f&&"string"===typeof e&&(e=$h(e),ub(e,f),e=b.postBodyFormat&&"JSON"==b.postBodyFormat?JSON.stringify(e):ac(e));f=e||f&&!nb(f);!ki&&f&&"POST"!=
b.method&&(ki=!0,qh(Error("AJAX request with postData should use POST")));return e}
function ri(a,b,c,d){var e=null;switch(b){case "JSON":try{var f=c.responseText}catch(g){throw d=Error("Error reading responseText"),d.params=a,rh(d),g;}a=c.getResponseHeader("Content-Type")||"";f&&0<=a.indexOf("json")&&(")]}'\n"===f.substring(0,5)&&(f=f.substring(5)),e=JSON.parse(f));break;case "XML":if(a=(a=c.responseXML)?si(a):null)e={},cb(a.getElementsByTagName("*"),function(g){e[g.tagName]=ti(g)})}d&&ui(e);
return e}
function ui(a){if(Ma(a))for(var b in a){var c;(c="html_content"==b)||(c=b.length-5,c=0<=c&&b.indexOf("_html",c)==c);if(c){c=b;Ab("HTML that is escaped and sanitized server-side and passed through yt.net.ajax");var d=a[b],e=wb();d=e?e.createHTML(d):d;a[c]=new Pb(d)}else ui(a[b])}}
function si(a){return a?(a=("responseXML"in a?a.responseXML:a).getElementsByTagName("root"))&&0<a.length?a[0]:null:null}
function ti(a){var b="";cb(a.childNodes,function(c){b+=c.nodeValue});
return b}
function qi(a,b,c,d,e,f,g){function h(){4==(k&&"readyState"in k?k.readyState:0)&&b&&ph(b)(k)}
c=void 0===c?"GET":c;d=void 0===d?"":d;var k=gi();if(!k)return null;"onloadend"in k?k.addEventListener("loadend",h,!1):k.onreadystatechange=h;L("debug_forward_web_query_parameters")&&(a=mi(a));k.open(c,a,!0);f&&(k.responseType=f);g&&(k.withCredentials=!0);c="POST"==c&&(void 0===window.FormData||!(d instanceof FormData));if(e=li(a,e))for(var m in e)k.setRequestHeader(m,e[m]),"content-type"==m.toLowerCase()&&(c=!1);c&&k.setRequestHeader("Content-Type","application/x-www-form-urlencoded");k.send(d);
return k}
;var vi=y.ytPubsubPubsubInstance||new J,wi=y.ytPubsubPubsubSubscribedKeys||{},xi=y.ytPubsubPubsubTopicToKeys||{},yi=y.ytPubsubPubsubIsSynchronous||{};function zi(a,b){var c=Ai();if(c&&b){var d=c.subscribe(a,function(){var e=arguments;var f=function(){wi[d]&&b.apply&&"function"==typeof b.apply&&b.apply(window,e)};
try{yi[a]?f():Bh(f,0)}catch(g){qh(g)}},void 0);
wi[d]=!0;xi[a]||(xi[a]=[]);xi[a].push(d);return d}return 0}
function Bi(a){var b=Ai();b&&("number"===typeof a?a=[a]:"string"===typeof a&&(a=[parseInt(a,10)]),cb(a,function(c){b.unsubscribeByKey(c);delete wi[c]}))}
function Ci(a,b){var c=Ai();c&&c.publish.apply(c,arguments)}
function Di(a){var b=Ai();if(b)if(b.clear(a),a)Ei(a);else for(var c in xi)Ei(c)}
function Ai(){return y.ytPubsubPubsubInstance}
function Ei(a){xi[a]&&(a=xi[a],cb(a,function(b){wi[b]&&delete wi[b]}),a.length=0)}
J.prototype.subscribe=J.prototype.subscribe;J.prototype.unsubscribeByKey=J.prototype.wa;J.prototype.publish=J.prototype.ka;J.prototype.clear=J.prototype.clear;z("ytPubsubPubsubInstance",vi,void 0);z("ytPubsubPubsubTopicToKeys",xi,void 0);z("ytPubsubPubsubIsSynchronous",yi,void 0);z("ytPubsubPubsubSubscribedKeys",wi,void 0);var Fi=window,M=Fi.ytcsi&&Fi.ytcsi.now?Fi.ytcsi.now:Fi.performance&&Fi.performance.timing&&Fi.performance.now&&Fi.performance.timing.navigationStart?function(){return Fi.performance.timing.navigationStart+Fi.performance.now()}:function(){return(new Date).getTime()};var Gi=gh("initial_gel_batch_timeout",2E3),Hi=Math.pow(2,16)-1,Ii=void 0;function Ji(){this.j=this.h=this.i=0}
var Ki=new Ji,Li=new Ji,Mi=!0,Ni=y.ytLoggingTransportGELQueue_||new Map;z("ytLoggingTransportGELQueue_",Ni,void 0);var Oi=y.ytLoggingTransportGELProtoQueue_||new Map;z("ytLoggingTransportGELProtoQueue_",Oi,void 0);var Pi=y.ytLoggingTransportTokensToCttTargetIds_||{};z("ytLoggingTransportTokensToCttTargetIds_",Pi,void 0);var Qi=y.ytLoggingTransportTokensToJspbCttTargetIds_||{};z("ytLoggingTransportTokensToJspbCttTargetIds_",Qi,void 0);
function Ri(a,b){if("log_event"===a.endpoint){var c=Si(a),d=Ni.get(c)||[];Ni.set(c,d);d.push(a.payload);Ti(b,d,c)}}
function Ui(a,b){if("log_event"===a.endpoint){var c=Si(a,!0),d=Oi.get(c)||[];Oi.set(c,d);d.push(a.payload);Ti(b,d,c,!0)}}
function Ti(a,b,c,d){d=void 0===d?!1:d;a&&(Ii=new a);a=gh("tvhtml5_logging_max_batch")||gh("web_logging_max_batch")||100;var e=M(),f=d?Li.j:Ki.j;b.length>=a?Vi({writeThenSend:!0},L("flush_only_full_queue")?c:void 0,d):10<=e-f&&(Wi(d),d?Li.j=e:Ki.j=e)}
function Xi(a,b){if("log_event"===a.endpoint){var c=Si(a),d=new Map;d.set(c,[a.payload]);b&&(Ii=new b);return new tf(function(e){Ii&&Ii.isReady()?Yi(d,e,{bypassNetworkless:!0},!0):e()})}}
function Zi(a,b){if("log_event"===a.endpoint){var c=Si(a,!0),d=new Map;d.set(c,[a.payload]);b&&(Ii=new b);return new tf(function(e){Ii&&Ii.isReady()?$i(d,e,{bypassNetworkless:!0},!0):e()})}}
function Si(a,b){var c="";if(a.xa)c="visitorOnlyApprovedKey";else if(a.cttAuthInfo){if(void 0===b?0:b){b=a.cttAuthInfo.token;c=a.cttAuthInfo;var d=new Lg;c.videoId?d.setVideoId(c.videoId):c.playlistId&&Oc(d,2,Mg,c.playlistId);Qi[b]=d}else b=a.cttAuthInfo,c={},b.videoId?c.videoId=b.videoId:b.playlistId&&(c.playlistId=b.playlistId),Pi[a.cttAuthInfo.token]=c;c=a.cttAuthInfo.token}return c}
function Vi(a,b,c){a=void 0===a?{}:a;c=void 0===c?!1:c;new tf(function(d){c?(Ch(Li.i),Ch(Li.h),Li.h=0):(Ch(Ki.i),Ch(Ki.h),Ki.h=0);if(Ii&&Ii.isReady())if(void 0!==b)if(c){var e=new Map,f=Oi.get(b)||[];e.set(b,f);$i(e,d,a);Oi.delete(b)}else e=new Map,f=Ni.get(b)||[],e.set(b,f),Yi(e,d,a),Ni.delete(b);else c?($i(Oi,d,a),Oi.clear()):(Yi(Ni,d,a),Ni.clear());else Wi(c),d()})}
function Wi(a){a=void 0===a?!1:a;if(L("web_gel_timeout_cap")&&(!a&&!Ki.h||a&&!Li.h)){var b=Bh(function(){Vi({writeThenSend:!0},void 0,a)},6E4);
a?Li.h=b:Ki.h=b}Ch(a?Li.i:Ki.i);b=B("LOGGING_BATCH_TIMEOUT",gh("web_gel_debounce_ms",1E4));L("shorten_initial_gel_batch_timeout")&&Mi&&(b=Gi);b=Bh(function(){Vi({writeThenSend:!0},void 0,a)},b);
a?Li.i=b:Ki.i=b}
function Yi(a,b,c,d){var e=Ii;c=void 0===c?{}:c;var f=Math.round(M()),g=a.size;a=q(a);for(var h=a.next();!h.done;h=a.next()){var k=q(h.value);h=k.next().value;var m=k=k.next().value;k=sb({context:aj(e.config_||bj())});k.events=m;(m=Pi[h])&&cj(k,h,m);delete Pi[h];h="visitorOnlyApprovedKey"===h;dj(k,f,h);ej(c);fj(e,"log_event",k,gj(c,h,function(){g--;g||b()},function(){g--;
g||b()},d));
Mi=!1}}
function $i(a,b,c,d){var e=Ii;c=void 0===c?{}:c;var f=Math.round(M()),g=a.size;a=q(a);for(var h=a.next();!h.done;h=a.next()){var k=q(h.value);h=k.next().value;var m=k=k.next().value;k=new Ng;var n=hj(e.config_||bj());H(k,1,n);for(n=0;n<m.length;n++)Uc(k,3,Jg,m[n]);(m=Qi[h])&&ij(k,h,m);delete Qi[h];h="visitorOnlyApprovedKey"===h;jj(k,f,h);ej(c);a:{Ec=!0;try{var t=JSON.stringify(k.toJSON(),Vc);break a}finally{Ec=!1}t=void 0}k=t;h=gj(c,h,function(){g--;g||b()},function(){g--;
g||b()},d);
h.headers={"Content-Type":"application/json+protobuf"};h.postBodyFormat="JSPB";h.postBody=k;fj(e,"log_event","",h);Mi=!1}}
function ej(a){L("always_send_and_write")&&(a.writeThenSend=!1)}
function gj(a,b,c,d,e){return{retry:!0,onSuccess:c,onError:d,Bb:a,xa:b,po:!!e,headers:{},postBodyFormat:"",postBody:""}}
function dj(a,b,c){a.requestTimeMs=String(b);L("unsplit_gel_payloads_in_logs")&&(a.unsplitGelPayloadsInLogs=!0);!c&&(b=B("EVENT_ID",void 0))&&(c=kj(),a.serializedClientEventId={serializedEventId:b,clientCounter:String(c)})}
function jj(a,b,c){G(a,2,b);if(!c&&(b=B("EVENT_ID",void 0))){c=kj();var d=new Kg;G(d,1,b);G(d,2,c);H(a,5,d)}}
function kj(){var a=B("BATCH_CLIENT_COUNTER",void 0)||0;a||(a=Math.floor(Math.random()*Hi/2));a++;a>Hi&&(a=1);dh("BATCH_CLIENT_COUNTER",a);return a}
function cj(a,b,c){if(c.videoId)var d="VIDEO";else if(c.playlistId)d="PLAYLIST";else return;a.credentialTransferTokenTargetId=c;a.context=a.context||{};a.context.user=a.context.user||{};a.context.user.credentialTransferTokens=[{token:b,scope:d}]}
function ij(a,b,c){if(Mc(c,1===Pc(c,Mg)?1:-1))var d=1;else if(c.getPlaylistId())d=2;else return;H(a,4,c);a=Qc(a,ng,1)||new ng;c=Qc(a,lg,3)||new lg;var e=new kg;e.setToken(b);G(e,1,d);Uc(c,12,kg,e);H(a,3,c)}
;var lj=y.ytLoggingGelSequenceIdObj_||{};z("ytLoggingGelSequenceIdObj_",lj,void 0);
function mj(a,b,c,d){d=void 0===d?{}:d;if(L("lr_drop_other_and_business_payloads")){if(kh[a]||jh[a])return}else if(L("lr_drop_other_payloads")&&kh[a])return;var e={},f=Math.round(d.timestamp||M());e.eventTimeMs=f<Number.MAX_SAFE_INTEGER?f:0;e[a]=b;a=Qh();e.context={lastActivityMs:String(d.timestamp||!isFinite(a)?-1:a)};L("log_sequence_info_on_gel_web")&&d.W&&(a=e.context,b=d.W,b={index:nj(b),groupKey:b},a.sequence=b,d.ub&&delete lj[d.W]);(d.nc?Xi:Ri)({endpoint:"log_event",payload:e,cttAuthInfo:d.cttAuthInfo,
xa:d.xa},c)}
function nj(a){lj[a]=a in lj?lj[a]+1:0;return lj[a]}
;function oj(a){var b=this;this.h=void 0;a.addEventListener("beforeinstallprompt",function(c){c.preventDefault();b.h=c})}
function pj(){if(!y.matchMedia)return"WEB_DISPLAY_MODE_UNKNOWN";try{return y.matchMedia("(display-mode: standalone)").matches?"WEB_DISPLAY_MODE_STANDALONE":y.matchMedia("(display-mode: minimal-ui)").matches?"WEB_DISPLAY_MODE_MINIMAL_UI":y.matchMedia("(display-mode: fullscreen)").matches?"WEB_DISPLAY_MODE_FULLSCREEN":y.matchMedia("(display-mode: browser)").matches?"WEB_DISPLAY_MODE_BROWSER":"WEB_DISPLAY_MODE_UNKNOWN"}catch(a){return"WEB_DISPLAY_MODE_UNKNOWN"}}
function qj(){var a=pj();a=Object.keys(ve).indexOf(a);return-1===a?null:a}
;function rj(a,b,c,d,e){Fd.set(""+a,b,{Qa:c,path:"/",domain:void 0===d?"youtube.com":d,secure:void 0===e?!1:e})}
function sj(a){return Fd.get(""+a,void 0)}
function tj(){if(!Fd.isEnabled())return!1;if(!Fd.isEmpty())return!0;Fd.set("TESTCOOKIESENABLED","1",{Qa:60});if("1"!==Fd.get("TESTCOOKIESENABLED"))return!1;Fd.remove("TESTCOOKIESENABLED");return!0}
;var uj=A("ytglobal.prefsUserPrefsPrefs_")||{};z("ytglobal.prefsUserPrefsPrefs_",uj,void 0);function vj(){this.h=B("ALT_PREF_COOKIE_NAME","PREF");this.i=B("ALT_PREF_COOKIE_DOMAIN","youtube.com");var a=sj(this.h);if(a){a=decodeURIComponent(a).split("&");for(var b=0;b<a.length;b++){var c=a[b].split("="),d=c[0];(c=c[1])&&(uj[d]=c.toString())}}}
vj.prototype.get=function(a,b){wj(a);xj(a);a=void 0!==uj[a]?uj[a].toString():null;return null!=a?a:b?b:""};
vj.prototype.set=function(a,b){wj(a);xj(a);if(null==b)throw Error("ExpectedNotNull");uj[a]=b.toString()};
function yj(a){return!!((zj("f"+(Math.floor(a/31)+1))||0)&1<<a%31)}
vj.prototype.remove=function(a){wj(a);xj(a);delete uj[a]};
vj.prototype.clear=function(){for(var a in uj)delete uj[a]};
function xj(a){if(/^f([1-9][0-9]*)$/.test(a))throw Error("ExpectedRegexMatch: "+a);}
function wj(a){if(!/^\w+$/.test(a))throw Error("ExpectedRegexMismatch: "+a);}
function zj(a){a=void 0!==uj[a]?uj[a].toString():null;return null!=a&&/^[A-Fa-f0-9]+$/.test(a)?parseInt(a,16):null}
Ja(vj);var Aj={bluetooth:"CONN_DISCO",cellular:"CONN_CELLULAR_UNKNOWN",ethernet:"CONN_WIFI",none:"CONN_NONE",wifi:"CONN_WIFI",wimax:"CONN_CELLULAR_4G",other:"CONN_UNKNOWN",unknown:"CONN_UNKNOWN","slow-2g":"CONN_CELLULAR_2G","2g":"CONN_CELLULAR_2G","3g":"CONN_CELLULAR_3G","4g":"CONN_CELLULAR_4G"},Bj={CONN_DEFAULT:0,CONN_UNKNOWN:1,CONN_NONE:2,CONN_WIFI:3,CONN_CELLULAR_2G:4,CONN_CELLULAR_3G:5,CONN_CELLULAR_4G:6,CONN_CELLULAR_UNKNOWN:7,CONN_DISCO:8,CONN_CELLULAR_5G:9,CONN_WIFI_METERED:10,CONN_CELLULAR_5G_SA:11,
CONN_CELLULAR_5G_NSA:12,CONN_INVALID:31},Cj={EFFECTIVE_CONNECTION_TYPE_UNKNOWN:0,EFFECTIVE_CONNECTION_TYPE_OFFLINE:1,EFFECTIVE_CONNECTION_TYPE_SLOW_2G:2,EFFECTIVE_CONNECTION_TYPE_2G:3,EFFECTIVE_CONNECTION_TYPE_3G:4,EFFECTIVE_CONNECTION_TYPE_4G:5},Dj={"slow-2g":"EFFECTIVE_CONNECTION_TYPE_SLOW_2G","2g":"EFFECTIVE_CONNECTION_TYPE_2G","3g":"EFFECTIVE_CONNECTION_TYPE_3G","4g":"EFFECTIVE_CONNECTION_TYPE_4G"};function Ej(){var a=y.navigator;return a?a.connection:void 0}
function Fj(){var a=Ej();if(a){var b=Aj[a.type||"unknown"]||"CONN_UNKNOWN";a=Aj[a.effectiveType||"unknown"]||"CONN_UNKNOWN";"CONN_CELLULAR_UNKNOWN"===b&&"CONN_UNKNOWN"!==a&&(b=a);if("CONN_UNKNOWN"!==b)return b;if("CONN_UNKNOWN"!==a)return a}}
function Gj(){var a=Ej();if(null!==a&&void 0!==a&&a.effectiveType)return Dj.hasOwnProperty(a.effectiveType)?Dj[a.effectiveType]:"EFFECTIVE_CONNECTION_TYPE_UNKNOWN"}
;function Hj(){return"INNERTUBE_API_KEY"in Yg&&"INNERTUBE_API_VERSION"in Yg}
function bj(){return{innertubeApiKey:B("INNERTUBE_API_KEY",void 0),innertubeApiVersion:B("INNERTUBE_API_VERSION",void 0),cb:B("INNERTUBE_CONTEXT_CLIENT_CONFIG_INFO"),eb:B("INNERTUBE_CONTEXT_CLIENT_NAME","WEB"),Wb:B("INNERTUBE_CONTEXT_CLIENT_NAME",1),innertubeContextClientVersion:B("INNERTUBE_CONTEXT_CLIENT_VERSION",void 0),yb:B("INNERTUBE_CONTEXT_HL",void 0),xb:B("INNERTUBE_CONTEXT_GL",void 0),Xb:B("INNERTUBE_HOST_OVERRIDE",void 0)||"",Zb:!!B("INNERTUBE_USE_THIRD_PARTY_AUTH",!1),Yb:!!B("INNERTUBE_OMIT_API_KEY_WHEN_AUTH_HEADER_IS_PRESENT",
!1),appInstallData:B("SERIALIZED_CLIENT_CONFIG_DATA",void 0)}}
function aj(a){var b={client:{hl:a.yb,gl:a.xb,clientName:a.eb,clientVersion:a.innertubeContextClientVersion,configInfo:a.cb}};navigator.userAgent&&(b.client.userAgent=String(navigator.userAgent));var c=y.devicePixelRatio;c&&1!=c&&(b.client.screenDensityFloat=String(c));c=hh();""!==c&&(b.client.experimentsToken=c);c=ih();0<c.length&&(b.request={internalExperimentFlags:c});Ij(a,void 0,b);Jj(a,void 0,b);Kj(void 0,b);Lj(a,void 0,b);Mj(void 0,b);B("DELEGATED_SESSION_ID")&&!L("pageid_as_header_web")&&(b.user=
{onBehalfOfUser:B("DELEGATED_SESSION_ID")});a=Object;c=a.assign;for(var d=b.client,e={},f=q(Object.entries($h(B("DEVICE","")))),g=f.next();!g.done;g=f.next()){var h=q(g.value);g=h.next().value;h=h.next().value;"cbrand"===g?e.deviceMake=h:"cmodel"===g?e.deviceModel=h:"cbr"===g?e.browserName=h:"cbrver"===g?e.browserVersion=h:"cos"===g?e.osName=h:"cosver"===g?e.osVersion=h:"cplatform"===g&&(e.platform=h)}b.client=c.call(a,d,e);return b}
function hj(a){var b=new ng,c=new gg;G(c,1,a.yb);G(c,2,a.xb);G(c,16,a.Wb);G(c,17,a.innertubeContextClientVersion);if(a.cb){var d=a.cb,e=new dg;d.coldConfigData&&G(e,1,d.coldConfigData);d.appInstallData&&G(e,6,d.appInstallData);d.coldHashData&&G(e,3,d.coldHashData);d.hotHashData&&G(e,5,d.hotHashData);H(c,62,e)}(d=y.devicePixelRatio)&&1!=d&&G(c,65,d);d=hh();""!==d&&G(c,54,d);d=ih();if(0<d.length){e=new ig;for(var f=0;f<d.length;f++){var g=new bg;G(g,1,d[f].key);g.setValue(d[f].value);Uc(e,15,bg,g)}H(b,
5,e)}Ij(a,c);Jj(a,c);Kj(c);Lj(a,c);Mj(c);B("DELEGATED_SESSION_ID")&&!L("pageid_as_header_web")&&(a=new lg,G(a,3,B("DELEGATED_SESSION_ID")));a=q(Object.entries($h(B("DEVICE",""))));for(d=a.next();!d.done;d=a.next())e=q(d.value),d=e.next().value,e=e.next().value,"cbrand"===d?G(c,12,e):"cmodel"===d?G(c,13,e):"cbr"===d?G(c,87,e):"cbrver"===d?G(c,88,e):"cos"===d?G(c,18,e):"cosver"===d?G(c,19,e):"cplatform"===d&&G(c,42,e);H(b,1,c);return b}
function Ij(a,b,c){a=a.eb;if("WEB"===a||"MWEB"===a||1===a||2===a)if(b){c=Qc(b,eg,96)||new eg;var d=qj();null!==d&&G(c,3,d);H(b,96,c)}else c&&(c.client.mainAppWebInfo=null!=(d=c.client.mainAppWebInfo)?d:{},c.client.mainAppWebInfo.webDisplayMode=pj())}
function Jj(a,b,c){a=a.eb;if(("WEB_REMIX"===a||76===a)&&!L("music_web_display_mode_killswitch"))if(b){var d;c=null!=(d=Qc(b,fg,70))?d:new fg;d=qj();null!==d&&G(c,10,d);H(b,70,c)}else if(c){var e;c.client.Ab=null!=(e=c.client.Ab)?e:{};c.client.Ab.webDisplayMode=pj()}}
function Kj(a,b){var c;if(L("web_log_memory_total_kbytes")&&(null==(c=y.navigator)?0:c.deviceMemory)){var d;c=null==(d=y.navigator)?void 0:d.deviceMemory;a?G(a,95,1E6*c):b&&(b.client.memoryTotalKbytes=""+1E6*c)}}
function Lj(a,b,c){if(a.appInstallData)if(b){var d;c=null!=(d=Qc(b,dg,62))?d:new dg;G(c,6,a.appInstallData);H(b,62,c)}else c&&(c.client.configInfo=c.client.configInfo||{},c.client.configInfo.appInstallData=a.appInstallData)}
function Mj(a,b){var c=Fj();c&&(a?G(a,61,Bj[c]):b&&(b.client.connectionType=c));L("web_log_effective_connection_type")&&(c=Gj())&&(a?G(a,94,Cj[c]):b&&(b.client.effectiveConnectionType=c))}
function Nj(a,b,c){c=void 0===c?{}:c;var d={"X-Goog-Visitor-Id":c.visitorData||B("VISITOR_DATA","")};if(b&&b.includes("www.youtube-nocookie.com"))return d;(b=c.no||B("AUTHORIZATION"))||(a?b="Bearer "+A("gapi.auth.getToken")().mo:b=Jd([]));b&&(d.Authorization=b,d["X-Goog-AuthUser"]=B("SESSION_INDEX",0),L("pageid_as_header_web")&&(d["X-Goog-PageId"]=B("DELEGATED_SESSION_ID")));return d}
;function Oj(a){a=Object.assign({},a);delete a.Authorization;var b=Jd();if(b){var c=new Qe;c.update(B("INNERTUBE_API_KEY",void 0));c.update(b);a.hash=zc(c.digest(),3)}return a}
;function Pj(a){var b=new Vf;(b=b.isAvailable()?a?new ag(b,a):b:null)||(a=new Wf(a||"UserDataSharedStore"),b=a.isAvailable()?a:null);this.h=(a=b)?new Rf(a):null;this.i=document.domain||window.location.hostname}
Pj.prototype.set=function(a,b,c,d){c=c||31104E3;this.remove(a);if(this.h)try{this.h.set(a,b,Date.now()+1E3*c);return}catch(f){}var e="";if(d)try{e=escape(mf(b))}catch(f){return}else e=escape(b);rj(a,e,c,this.i)};
Pj.prototype.get=function(a,b){var c=void 0,d=!this.h;if(!d)try{c=this.h.get(a)}catch(e){d=!0}if(d&&(c=sj(a))&&(c=unescape(c),b))try{c=JSON.parse(c)}catch(e){this.remove(a),c=void 0}return c};
Pj.prototype.remove=function(a){this.h&&this.h.remove(a);var b=this.i;Fd.remove(""+a,"/",void 0===b?"youtube.com":b)};var Qj;function Rj(){Qj||(Qj=new Pj("yt.innertube"));return Qj}
function Sj(a,b,c,d){if(d)return null;d=Rj().get("nextId",!0)||1;var e=Rj().get("requests",!0)||{};e[d]={method:a,request:b,authState:Oj(c),requestTime:Math.round(M())};Rj().set("nextId",d+1,86400,!0);Rj().set("requests",e,86400,!0);return d}
function Tj(a){var b=Rj().get("requests",!0)||{};delete b[a];Rj().set("requests",b,86400,!0)}
function Uj(a){var b=Rj().get("requests",!0);if(b){for(var c in b){var d=b[c];if(!(6E4>Math.round(M())-d.requestTime)){var e=d.authState,f=Oj(Nj(!1));qb(e,f)&&(e=d.request,"requestTimeMs"in e&&(e.requestTimeMs=Math.round(M())),fj(a,d.method,e,{}));delete b[c]}}Rj().set("requests",b,86400,!0)}}
;var Vj=uc||vc;function Wj(a){var b=Mb();return b?0<=b.toLowerCase().indexOf(a):!1}
;var Xj=function(){var a;return function(){a||(a=new Pj("ytidb"));return a}}();
function Yj(){var a;return null===(a=Xj())||void 0===a?void 0:a.get("LAST_RESULT_ENTRY_KEY",!0)}
;var Zj=[],ak,bk=!1;function ck(){var a={};for(ak=new dk(void 0===a.handleError?ek:a.handleError,void 0===a.logEvent?fk:a.logEvent);0<Zj.length;)switch(a=Zj.shift(),a.type){case "ERROR":ak.handleError(a.payload);break;case "EVENT":ak.logEvent(a.eventType,a.payload)}}
function gk(a){bk||(ak?ak.handleError(a):(Zj.push({type:"ERROR",payload:a}),10<Zj.length&&Zj.shift()))}
function hk(a,b){bk||(ak?ak.logEvent(a,b):(Zj.push({type:"EVENT",eventType:a,payload:b}),10<Zj.length&&Zj.shift()))}
;function ik(a){var b=Da.apply(1,arguments);var c=Error.call(this,a);this.message=c.message;"stack"in c&&(this.stack=c.stack);this.args=[].concat(ha(b))}
r(ik,Error);function jk(){try{return kk(),!0}catch(a){return!1}}
function kk(){if(void 0!==B("DATASYNC_ID",void 0))return B("DATASYNC_ID",void 0);throw new ik("Datasync ID not set","unknown");}
;function lk(a){if(0<=a.indexOf(":"))throw Error("Database name cannot contain ':'");}
function mk(a){return a.substr(0,a.indexOf(":"))||a}
;var nk={},ok=(nk.AUTH_INVALID="No user identifier specified.",nk.EXPLICIT_ABORT="Transaction was explicitly aborted.",nk.IDB_NOT_SUPPORTED="IndexedDB is not supported.",nk.MISSING_INDEX="Index not created.",nk.MISSING_OBJECT_STORES="Object stores not created.",nk.DB_DELETED_BY_MISSING_OBJECT_STORES="Database is deleted because expected object stores were not created.",nk.DB_REOPENED_BY_MISSING_OBJECT_STORES="Database is reopened because expected object stores were not created.",nk.UNKNOWN_ABORT="Transaction was aborted for unknown reasons.",
nk.QUOTA_EXCEEDED="The current transaction exceeded its quota limitations.",nk.QUOTA_MAYBE_EXCEEDED="The current transaction may have failed because of exceeding quota limitations.",nk.EXECUTE_TRANSACTION_ON_CLOSED_DB="Can't start a transaction on a closed database",nk.INCOMPATIBLE_DB_VERSION="The binary is incompatible with the database version",nk),pk={},qk=(pk.AUTH_INVALID="ERROR",pk.EXECUTE_TRANSACTION_ON_CLOSED_DB="WARNING",pk.EXPLICIT_ABORT="IGNORED",pk.IDB_NOT_SUPPORTED="ERROR",pk.MISSING_INDEX=
"WARNING",pk.MISSING_OBJECT_STORES="ERROR",pk.DB_DELETED_BY_MISSING_OBJECT_STORES="WARNING",pk.DB_REOPENED_BY_MISSING_OBJECT_STORES="WARNING",pk.QUOTA_EXCEEDED="WARNING",pk.QUOTA_MAYBE_EXCEEDED="WARNING",pk.UNKNOWN_ABORT="WARNING",pk.INCOMPATIBLE_DB_VERSION="WARNING",pk),rk={},sk=(rk.AUTH_INVALID=!1,rk.EXECUTE_TRANSACTION_ON_CLOSED_DB=!1,rk.EXPLICIT_ABORT=!1,rk.IDB_NOT_SUPPORTED=!1,rk.MISSING_INDEX=!1,rk.MISSING_OBJECT_STORES=!1,rk.DB_DELETED_BY_MISSING_OBJECT_STORES=!1,rk.DB_REOPENED_BY_MISSING_OBJECT_STORES=
!1,rk.QUOTA_EXCEEDED=!1,rk.QUOTA_MAYBE_EXCEEDED=!0,rk.UNKNOWN_ABORT=!0,rk.INCOMPATIBLE_DB_VERSION=!1,rk);function tk(a,b,c,d,e){b=void 0===b?{}:b;c=void 0===c?ok[a]:c;d=void 0===d?qk[a]:d;e=void 0===e?sk[a]:e;ik.call(this,c,Object.assign({name:"YtIdbKnownError",isSw:void 0===self.document,isIframe:self!==self.top,type:a},b));this.type=a;this.message=c;this.level=d;this.h=e;Object.setPrototypeOf(this,tk.prototype)}
r(tk,ik);function uk(a,b){tk.call(this,"MISSING_OBJECT_STORES",{expectedObjectStores:b,foundObjectStores:a},ok.MISSING_OBJECT_STORES);Object.setPrototypeOf(this,uk.prototype)}
r(uk,tk);function vk(a,b){var c=Error.call(this);this.message=c.message;"stack"in c&&(this.stack=c.stack);this.index=a;this.objectStore=b;Object.setPrototypeOf(this,vk.prototype)}
r(vk,Error);var wk=["The database connection is closing","Can't start a transaction on a closed database","A mutation operation was attempted on a database that did not allow mutations"];
function xk(a,b,c,d){b=mk(b);var e=a instanceof Error?a:Error("Unexpected error: "+a);if(e instanceof tk)return e;a={objectStoreNames:c,dbName:b,dbVersion:d};if("QuotaExceededError"===e.name)return new tk("QUOTA_EXCEEDED",a);if(wc&&"UnknownError"===e.name)return new tk("QUOTA_MAYBE_EXCEEDED",a);if(e instanceof vk)return new tk("MISSING_INDEX",Object.assign(Object.assign({},a),{objectStore:e.objectStore,index:e.index}));if("InvalidStateError"===e.name&&wk.some(function(f){return e.message.includes(f)}))return new tk("EXECUTE_TRANSACTION_ON_CLOSED_DB",
a);
if("AbortError"===e.name)return new tk("UNKNOWN_ABORT",a,e.message);e.args=[Object.assign(Object.assign({},a),{name:"IdbError",Cb:e.name})];e.level="WARNING";return e}
function yk(a,b,c){var d=Yj();return new tk("IDB_NOT_SUPPORTED",{context:{caller:a,publicName:b,version:c,hasSucceededOnce:null===d||void 0===d?void 0:d.hasSucceededOnce}})}
;function zk(a){if(!a)throw Error();throw a;}
function Ak(a){return a}
function Bk(a){this.h=a}
function Ck(a){function b(e){if("PENDING"===d.state.status){d.state={status:"REJECTED",reason:e};e=q(d.onRejected);for(var f=e.next();!f.done;f=e.next())f=f.value,f()}}
function c(e){if("PENDING"===d.state.status){d.state={status:"FULFILLED",value:e};e=q(d.h);for(var f=e.next();!f.done;f=e.next())f=f.value,f()}}
var d=this;this.state={status:"PENDING"};this.h=[];this.onRejected=[];a=a.h;try{a(c,b)}catch(e){b(e)}}
Ck.all=function(a){return new Ck(new Bk(function(b,c){var d=[],e=a.length;0===e&&b(d);for(var f={oa:0};f.oa<a.length;f={oa:f.oa},++f.oa)Dk(Ck.resolve(a[f.oa]).then(function(g){return function(h){d[g.oa]=h;e--;0===e&&b(d)}}(f)),function(g){c(g)})}))};
Ck.resolve=function(a){return new Ck(new Bk(function(b,c){a instanceof Ck?a.then(b,c):b(a)}))};
Ck.reject=function(a){return new Ck(new Bk(function(b,c){c(a)}))};
Ck.prototype.then=function(a,b){var c=this,d=null!==a&&void 0!==a?a:Ak,e=null!==b&&void 0!==b?b:zk;return new Ck(new Bk(function(f,g){"PENDING"===c.state.status?(c.h.push(function(){Ek(c,c,d,f,g)}),c.onRejected.push(function(){Fk(c,c,e,f,g)})):"FULFILLED"===c.state.status?Ek(c,c,d,f,g):"REJECTED"===c.state.status&&Fk(c,c,e,f,g)}))};
function Dk(a,b){a.then(void 0,b)}
function Ek(a,b,c,d,e){try{if("FULFILLED"!==a.state.status)throw Error("calling handleResolve before the promise is fulfilled.");var f=c(a.state.value);f instanceof Ck?Gk(a,b,f,d,e):d(f)}catch(g){e(g)}}
function Fk(a,b,c,d,e){try{if("REJECTED"!==a.state.status)throw Error("calling handleReject before the promise is rejected.");var f=c(a.state.reason);f instanceof Ck?Gk(a,b,f,d,e):d(f)}catch(g){e(g)}}
function Gk(a,b,c,d,e){b===c?e(new TypeError("Circular promise chain detected.")):c.then(function(f){f instanceof Ck?Gk(a,b,f,d,e):d(f)},function(f){e(f)})}
;function Hk(a,b,c){function d(){c(a.error);f()}
function e(){b(a.result);f()}
function f(){try{a.removeEventListener("success",e),a.removeEventListener("error",d)}catch(g){}}
a.addEventListener("success",e);a.addEventListener("error",d)}
function Ik(a){return new Promise(function(b,c){Hk(a,b,c)})}
function Jk(a){return new Ck(new Bk(function(b,c){Hk(a,b,c)}))}
;function Kk(a,b){return new Ck(new Bk(function(c,d){function e(){var f=a?b(a):null;f?f.then(function(g){a=g;e()},d):c()}
e()}))}
;function Lk(a,b){this.h=a;this.options=b;this.transactionCount=0;this.j=Math.round(M());this.i=!1}
l=Lk.prototype;l.add=function(a,b,c){return Mk(this,[a],{mode:"readwrite",O:!0},function(d){return d.objectStore(a).add(b,c)})};
l.clear=function(a){return Mk(this,[a],{mode:"readwrite",O:!0},function(b){return b.objectStore(a).clear()})};
l.close=function(){var a;this.h.close();(null===(a=this.options)||void 0===a?0:a.closed)&&this.options.closed()};
l.count=function(a,b){return Mk(this,[a],{mode:"readonly",O:!0},function(c){return c.objectStore(a).count(b)})};
function Nk(a,b,c){a=a.h.createObjectStore(b,c);return new Ok(a)}
l.delete=function(a,b){return Mk(this,[a],{mode:"readwrite",O:!0},function(c){return c.objectStore(a).delete(b)})};
l.get=function(a,b){return Mk(this,[a],{mode:"readonly",O:!0},function(c){return c.objectStore(a).get(b)})};
function Pk(a,b){return Mk(a,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(c){c=c.objectStore("LogsRequestsStore");return Jk(c.h.put(b,void 0))})}
l.objectStoreNames=function(){return Array.from(this.h.objectStoreNames)};
function Mk(a,b,c,d){var e,f,g,h,k,m,n,t,x,u,C,D;return w(function(K){switch(K.h){case 1:var N={mode:"readonly",O:!1,tag:"IDB_TRANSACTION_TAG_UNKNOWN"};"string"===typeof c?N.mode=c:Object.assign(N,c);e=N;a.transactionCount++;f=e.O?3:1;g=0;case 2:if(h){K.s(3);break}g++;k=Math.round(M());sa(K,4);m=a.h.transaction(b,e.mode);N=new Qk(m);N=Rk(N,d);return v(K,N,6);case 6:return n=K.i,t=Math.round(M()),Sk(a,k,t,g,void 0,b.join(),e),K.return(n);case 4:x=ua(K);u=Math.round(M());C=xk(x,a.h.name,b.join(),a.h.version);
if((D=C instanceof tk&&!C.h)||g>=f)Sk(a,k,u,g,C,b.join(),e),h=C;K.s(2);break;case 3:return K.return(Promise.reject(h))}})}
function Sk(a,b,c,d,e,f,g){b=c-b;e?(e instanceof tk&&("QUOTA_EXCEEDED"===e.type||"QUOTA_MAYBE_EXCEEDED"===e.type)&&hk("QUOTA_EXCEEDED",{dbName:mk(a.h.name),objectStoreNames:f,transactionCount:a.transactionCount,transactionMode:g.mode}),e instanceof tk&&"UNKNOWN_ABORT"===e.type&&(c-=a.j,0>c&&c>=Math.pow(2,31)&&(c=0),hk("TRANSACTION_UNEXPECTEDLY_ABORTED",{objectStoreNames:f,transactionDuration:b,transactionCount:a.transactionCount,dbDuration:c}),a.i=!0),Tk(a,!1,d,f,b,g.tag),gk(e)):Tk(a,!0,d,f,b,g.tag)}
function Tk(a,b,c,d,e,f){hk("TRANSACTION_ENDED",{objectStoreNames:d,connectionHasUnknownAbortedTransaction:a.i,duration:e,isSuccessful:b,tryCount:c,tag:void 0===f?"IDB_TRANSACTION_TAG_UNKNOWN":f})}
l.getName=function(){return this.h.name};
function Ok(a){this.h=a}
l=Ok.prototype;l.add=function(a,b){return Jk(this.h.add(a,b))};
l.autoIncrement=function(){return this.h.autoIncrement};
l.clear=function(){return Jk(this.h.clear()).then(function(){})};
l.count=function(a){return Jk(this.h.count(a))};
function Uk(a,b){return Vk(a,{query:b},function(c){return c.delete().then(function(){return c.continue()})}).then(function(){})}
l.delete=function(a){return a instanceof IDBKeyRange?Uk(this,a):Jk(this.h.delete(a))};
l.get=function(a){return Jk(this.h.get(a))};
l.index=function(a){try{return new Wk(this.h.index(a))}catch(b){if(b instanceof Error&&"NotFoundError"===b.name)throw new vk(a,this.h.name);throw b;}};
l.getName=function(){return this.h.name};
l.keyPath=function(){return this.h.keyPath};
function Vk(a,b,c){a=a.h.openCursor(b.query,b.direction);return Xk(a).then(function(d){return Kk(d,c)})}
function Qk(a){var b=this;this.h=a;this.j=new Map;this.i=!1;this.done=new Promise(function(c,d){b.h.addEventListener("complete",function(){c()});
b.h.addEventListener("error",function(e){e.currentTarget===e.target&&d(b.h.error)});
b.h.addEventListener("abort",function(){var e=b.h.error;if(e)d(e);else if(!b.i){e=tk;for(var f=b.h.objectStoreNames,g=[],h=0;h<f.length;h++){var k=f.item(h);if(null===k)throw Error("Invariant: item in DOMStringList is null");g.push(k)}e=new e("UNKNOWN_ABORT",{objectStoreNames:g.join(),dbName:b.h.db.name,mode:b.h.mode});d(e)}})})}
function Rk(a,b){var c=new Promise(function(d,e){try{Dk(b(a).then(function(f){d(f)}),e)}catch(f){e(f),a.abort()}});
return Promise.all([c,a.done]).then(function(d){return q(d).next().value})}
Qk.prototype.abort=function(){this.h.abort();this.i=!0;throw new tk("EXPLICIT_ABORT");};
Qk.prototype.objectStore=function(a){a=this.h.objectStore(a);var b=this.j.get(a);b||(b=new Ok(a),this.j.set(a,b));return b};
function Wk(a){this.h=a}
l=Wk.prototype;l.count=function(a){return Jk(this.h.count(a))};
l.delete=function(a){return Yk(this,{query:a},function(b){return b.delete().then(function(){return b.continue()})})};
l.get=function(a){return Jk(this.h.get(a))};
l.getKey=function(a){return Jk(this.h.getKey(a))};
l.keyPath=function(){return this.h.keyPath};
l.unique=function(){return this.h.unique};
function Yk(a,b,c){a=a.h.openCursor(void 0===b.query?null:b.query,void 0===b.direction?"next":b.direction);return Xk(a).then(function(d){return Kk(d,c)})}
function Zk(a,b){this.request=a;this.cursor=b}
function Xk(a){return Jk(a).then(function(b){return b?new Zk(a,b):null})}
l=Zk.prototype;l.advance=function(a){this.cursor.advance(a);return Xk(this.request)};
l.continue=function(a){this.cursor.continue(a);return Xk(this.request)};
l.delete=function(){return Jk(this.cursor.delete()).then(function(){})};
l.getKey=function(){return this.cursor.key};
l.getValue=function(){return this.cursor.value};
l.update=function(a){return Jk(this.cursor.update(a))};function $k(a,b,c){return new Promise(function(d,e){function f(){x||(x=new Lk(g.result,{closed:t}));return x}
var g=void 0!==b?self.indexedDB.open(a,b):self.indexedDB.open(a);var h=c.blocked,k=c.blocking,m=c.zc,n=c.upgrade,t=c.closed,x;g.addEventListener("upgradeneeded",function(u){try{if(null===u.newVersion)throw Error("Invariant: newVersion on IDbVersionChangeEvent is null");if(null===g.transaction)throw Error("Invariant: transaction on IDbOpenDbRequest is null");u.dataLoss&&"none"!==u.dataLoss&&hk("IDB_DATA_CORRUPTED",{reason:u.dataLossMessage||"unknown reason",dbName:mk(a)});var C=f(),D=new Qk(g.transaction);
n&&n(C,function(K){return u.oldVersion<K&&u.newVersion>=K},D);
D.done.catch(function(K){e(K)})}catch(K){e(K)}});
g.addEventListener("success",function(){var u=g.result;k&&u.addEventListener("versionchange",function(){k(f())});
u.addEventListener("close",function(){hk("IDB_UNEXPECTEDLY_CLOSED",{dbName:mk(a),dbVersion:u.version});m&&m()});
d(f())});
g.addEventListener("error",function(){e(g.error)});
h&&g.addEventListener("blocked",function(){h()})})}
function al(a,b,c){c=void 0===c?{}:c;return $k(a,b,c)}
function bl(a,b){b=void 0===b?{}:b;var c,d,e,f;return w(function(g){if(1==g.h)return sa(g,2),c=self.indexedDB.deleteDatabase(a),d=b,(e=d.blocked)&&c.addEventListener("blocked",function(){e()}),v(g,Ik(c),4);
if(2!=g.h)return ta(g,0);f=ua(g);throw xk(f,a,"",-1);})}
;function cl(a){return new Promise(function(b){Hh(function(){b()},a)})}
function dl(a,b){this.name=a;this.options=b;this.l=!0;this.m=this.o=0;this.i=500}
dl.prototype.j=function(a,b,c){c=void 0===c?{}:c;return al(a,b,c)};
dl.prototype.delete=function(a){a=void 0===a?{}:a;return bl(this.name,a)};
function el(a,b){return new tk("INCOMPATIBLE_DB_VERSION",{dbName:a.name,oldVersion:a.options.version,newVersion:b})}
function fl(a,b){if(!b)throw yk("openWithToken",mk(a.name));return a.open()}
dl.prototype.open=function(){function a(){var f,g,h,k,m,n,t,x,u,C;return w(function(D){switch(D.h){case 1:return h=null!==(f=Error().stack)&&void 0!==f?f:"",sa(D,2),v(D,c.j(c.name,c.options.version,e),4);case 4:k=D.i;for(var K=c.options,N=[],S=q(Object.keys(K.za)),W=S.next();!W.done;W=S.next()){W=W.value;var Qa=K.za[W],zb=void 0===Qa.ic?Number.MAX_VALUE:Qa.ic;!(k.h.version>=Qa.Xa)||k.h.version>=zb||k.h.objectStoreNames.contains(W)||N.push(W)}m=N;if(0===m.length){D.s(5);break}n=Object.keys(c.options.za);
t=k.objectStoreNames();if(c.m<gh("ytidb_reopen_db_retries",0))return c.m++,k.close(),gk(new tk("DB_REOPENED_BY_MISSING_OBJECT_STORES",{dbName:c.name,expectedObjectStores:n,foundObjectStores:t})),D.return(a());if(!(c.o<gh("ytidb_remake_db_retries",1))){D.s(6);break}c.o++;if(!L("ytidb_remake_db_enable_backoff_delay")){D.s(7);break}return v(D,cl(c.i),8);case 8:c.i*=2;case 7:return v(D,c.delete(),9);case 9:return gk(new tk("DB_DELETED_BY_MISSING_OBJECT_STORES",{dbName:c.name,expectedObjectStores:n,foundObjectStores:t})),
D.return(a());case 6:throw new uk(t,n);case 5:return D.return(k);case 2:x=ua(D);if(x instanceof DOMException?"VersionError"!==x.name:"DOMError"in self&&x instanceof DOMError?"VersionError"!==x.name:!(x instanceof Object&&"message"in x)||"An attempt was made to open a database using a lower version than the existing version."!==x.message){D.s(10);break}return v(D,c.j(c.name,void 0,Object.assign(Object.assign({},e),{upgrade:void 0})),11);case 11:u=D.i;C=u.h.version;if(void 0!==c.options.version&&C>
c.options.version+1)throw u.close(),c.l=!1,el(c,C);return D.return(u);case 10:throw b(),x instanceof Error&&!L("ytidb_async_stack_killswitch")&&(x.stack=x.stack+"\n"+h.substring(h.indexOf("\n")+1)),xk(x,c.name,"",null!==(g=c.options.version)&&void 0!==g?g:-1);}})}
function b(){c.h===d&&(c.h=void 0)}
var c=this;if(!this.l)throw el(this);if(this.h)return this.h;var d,e={blocking:function(f){f.close()},
closed:b,zc:b,upgrade:this.options.upgrade};return this.h=d=a()};var gl=new dl("YtIdbMeta",{za:{databases:{Xa:1}},upgrade:function(a,b){b(1)&&Nk(a,"databases",{keyPath:"actualName"})}});
function hl(a,b){var c;return w(function(d){if(1==d.h)return v(d,fl(gl,b),2);c=d.i;return d.return(Mk(c,["databases"],{O:!0,mode:"readwrite"},function(e){var f=e.objectStore("databases");return f.get(a.actualName).then(function(g){if(g?a.actualName!==g.actualName||a.publicName!==g.publicName||a.userIdentifier!==g.userIdentifier:1)return Jk(f.h.put(a,void 0)).then(function(){})})}))})}
function il(a,b){var c;return w(function(d){if(1==d.h)return a?v(d,fl(gl,b),2):d.return();c=d.i;return d.return(c.delete("databases",a))})}
function jl(a,b){var c,d;return w(function(e){return 1==e.h?(c=[],v(e,fl(gl,b),2)):3!=e.h?(d=e.i,v(e,Mk(d,["databases"],{O:!0,mode:"readonly"},function(f){c.length=0;return Vk(f.objectStore("databases"),{},function(g){a(g.getValue())&&c.push(g.getValue());return g.continue()})}),3)):e.return(c)})}
function kl(a){return jl(function(b){return"LogsDatabaseV2"===b.publicName&&void 0!==b.userIdentifier},a)}
function ll(a,b){return jl(function(c){return void 0!==c.userIdentifier&&!a.includes(c.userIdentifier)},b)}
;var ml,nl=new function(){}(new function(){});
function ol(){var a,b,c;return w(function(d){switch(d.h){case 1:a=Yj();if(null===a||void 0===a?0:a.hasSucceededOnce)return d.return(!0);var e;if(e=Vj)e=/WebKit\/([0-9]+)/.exec(Mb()),e=!!(e&&600<=parseInt(e[1],10));e&&(e=/WebKit\/([0-9]+)/.exec(Mb()),e=!(e&&602<=parseInt(e[1],10)));if(e||hc)return d.return(!1);try{if(b=self,!(b.indexedDB&&b.IDBIndex&&b.IDBKeyRange&&b.IDBObjectStore))return d.return(!1)}catch(f){return d.return(!1)}if(!("IDBTransaction"in self&&"objectStoreNames"in IDBTransaction.prototype))return d.return(!1);
sa(d,2);c={actualName:"yt-idb-test-do-not-use",publicName:"yt-idb-test-do-not-use",userIdentifier:void 0};return v(d,hl(c,nl),4);case 4:return v(d,il("yt-idb-test-do-not-use",nl),5);case 5:return d.return(!0);case 2:return ua(d),d.return(!1)}})}
function pl(){if(void 0!==ml)return ml;bk=!0;return ml=ol().then(function(a){bk=!1;var b,c;null!==(b=Xj())&&void 0!==b&&b.h&&(b=Yj(),b={hasSucceededOnce:(null===b||void 0===b?void 0:b.hasSucceededOnce)||a},null===(c=Xj())||void 0===c?void 0:c.set("LAST_RESULT_ENTRY_KEY",b,2592E3,!0));return a})}
function ql(){return A("ytglobal.idbToken_")||void 0}
function rl(){var a=ql();return a?Promise.resolve(a):pl().then(function(b){(b=b?nl:void 0)&&z("ytglobal.idbToken_",b,void 0);return b})}
;var sl=0;function tl(a){sl||(sl=Lh.M(function(){var b,c,d,e,f;return w(function(g){switch(g.h){case 1:return v(g,rl(),2);case 2:b=g.i;if(!b)return g.return();c=!0;sa(g,3);return v(g,ll(a,b),5);case 5:d=g.i;if(!d.length){c=!1;g.s(6);break}e=d[0];return v(g,bl(e.actualName),7);case 7:return v(g,il(e.actualName,b),6);case 6:ta(g,4);break;case 3:f=ua(g),gk(f),c=!1;case 4:Lh.U(sl),sl=0,c&&tl(a),g.h=0}})}))}
new Yc;function ul(a){if(!jk())throw a=new tk("AUTH_INVALID",{dbName:a}),gk(a),a;var b=kk();return{actualName:a+":"+b,publicName:a,userIdentifier:b}}
function vl(a,b,c,d){var e,f,g,h,k,m;return w(function(n){switch(n.h){case 1:return f=null!==(e=Error().stack)&&void 0!==e?e:"",v(n,rl(),2);case 2:g=n.i;if(!g)throw h=yk("openDbImpl",a,b),L("ytidb_async_stack_killswitch")||(h.stack=h.stack+"\n"+f.substring(f.indexOf("\n")+1)),gk(h),h;lk(a);k=c?{actualName:a,publicName:a,userIdentifier:void 0}:ul(a);sa(n,3);return v(n,hl(k,g),5);case 5:return v(n,al(k.actualName,b,d),6);case 6:return n.return(n.i);case 3:return m=ua(n),sa(n,7),v(n,il(k.actualName,
g),9);case 9:ta(n,8);break;case 7:ua(n);case 8:throw m;}})}
function wl(a,b,c){c=void 0===c?{}:c;return vl(a,b,!1,c)}
function xl(a,b,c){c=void 0===c?{}:c;return vl(a,b,!0,c)}
function yl(a,b){b=void 0===b?{}:b;var c,d;return w(function(e){if(1==e.h)return v(e,rl(),2);if(3!=e.h){c=e.i;if(!c)return e.return();lk(a);d=ul(a);return v(e,bl(d.actualName,b),3)}return v(e,il(d.actualName,c),0)})}
function zl(a,b,c){a=a.map(function(d){return w(function(e){return 1==e.h?v(e,bl(d.actualName,b),2):v(e,il(d.actualName,c),0)})});
return Promise.all(a).then(function(){})}
function Al(){var a=void 0===a?{}:a;var b,c;return w(function(d){if(1==d.h)return v(d,rl(),2);if(3!=d.h){b=d.i;if(!b)return d.return();lk("LogsDatabaseV2");return v(d,kl(b),3)}c=d.i;return v(d,zl(c,a,b),0)})}
function Bl(a,b){b=void 0===b?{}:b;var c;return w(function(d){if(1==d.h)return v(d,rl(),2);if(3!=d.h){c=d.i;if(!c)return d.return();lk(a);return v(d,bl(a,b),3)}return v(d,il(a,c),0)})}
;function Cl(a){var b,c,d,e,f,g,h,k;this.h=!1;this.potentialEsfErrorCounter=this.i=0;this.handleError=function(){};
this.sa=function(){};
this.now=Date.now;this.ya=!1;this.Kb=null!==(b=a.Kb)&&void 0!==b?b:100;this.Hb=null!==(c=a.Hb)&&void 0!==c?c:1;this.Fb=null!==(d=a.Fb)&&void 0!==d?d:2592E6;this.Db=null!==(e=a.Db)&&void 0!==e?e:12E4;this.Gb=null!==(f=a.Gb)&&void 0!==f?f:5E3;this.v=null!==(g=a.v)&&void 0!==g?g:void 0;this.Ma=!!a.Ma;this.La=null!==(h=a.La)&&void 0!==h?h:.1;this.Sa=null!==(k=a.Sa)&&void 0!==k?k:10;a.handleError&&(this.handleError=a.handleError);a.sa&&(this.sa=a.sa);a.ya&&(this.ya=a.ya);this.C=a.C;this.V=a.V;this.J=a.J;
this.I=a.I;this.ea=a.ea;this.ib=a.ib;this.hb=a.hb;this.v&&(!this.C||this.C("networkless_logging"))&&Dl(this)}
function Dl(a){a.v&&!a.ya&&(a.h=!0,a.Ma&&Math.random()<=a.La&&a.J.Qb(a.v),El(a),a.I.G()&&a.Ca(),a.I.ba(a.ib,a.Ca.bind(a)),a.I.ba(a.hb,a.nb.bind(a)))}
l=Cl.prototype;l.writeThenSend=function(a,b){var c=this;b=void 0===b?{}:b;if(this.v&&this.h){var d={url:a,options:b,timestamp:this.now(),status:"NEW",sendCount:0};this.J.set(d,this.v).then(function(e){d.id=e;c.I.G()&&Fl(c,d)}).catch(function(e){Fl(c,d);
Gl(c,e)})}else this.ea(a,b)};
l.sendThenWrite=function(a,b,c){var d=this;b=void 0===b?{}:b;if(this.v&&this.h){var e={url:a,options:b,timestamp:this.now(),status:"NEW",sendCount:0};this.C&&this.C("nwl_skip_retry")&&(e.skipRetry=c);if(this.I.G()||this.C&&this.C("nwl_aggressive_send_then_write")&&!e.skipRetry){if(!e.skipRetry){var f=b.onError?b.onError:function(){};
b.onError=function(g,h){return w(function(k){if(1==k.h)return v(k,d.J.set(e,d.v).catch(function(m){Gl(d,m)}),2);
f(g,h);k.h=0})}}this.ea(a,b,e.skipRetry)}else this.J.set(e,this.v).catch(function(g){d.ea(a,b,e.skipRetry);
Gl(d,g)})}else this.ea(a,b,this.C&&this.C("nwl_skip_retry")&&c)};
l.sendAndWrite=function(a,b){var c=this;b=void 0===b?{}:b;if(this.v&&this.h){var d={url:a,options:b,timestamp:this.now(),status:"NEW",sendCount:0},e=!1,f=b.onSuccess?b.onSuccess:function(){};
d.options.onSuccess=function(g,h){void 0!==d.id?c.J.ra(d.id,c.v):e=!0;c.I.ca&&c.C&&c.C("vss_network_hint")&&c.I.ca(!0);f(g,h)};
this.ea(d.url,d.options);this.J.set(d,this.v).then(function(g){d.id=g;e&&c.J.ra(d.id,c.v)}).catch(function(g){Gl(c,g)})}else this.ea(a,b)};
l.Ca=function(){var a=this;if(!this.v)throw yk("throttleSend");this.i||(this.i=this.V.M(function(){var b;return w(function(c){if(1==c.h)return v(c,a.J.wb("NEW",a.v),2);if(3!=c.h)return b=c.i,b?v(c,Fl(a,b),3):(a.nb(),c.return());a.i&&(a.i=0,a.Ca());c.h=0})},this.Kb))};
l.nb=function(){this.V.U(this.i);this.i=0};
function Fl(a,b){var c,d;return w(function(e){switch(e.h){case 1:if(!a.v)throw c=yk("immediateSend"),c;if(void 0===b.id){e.s(2);break}return v(e,a.J.ac(b.id,a.v),3);case 3:(d=e.i)?b=d:a.sa(Error("The request cannot be found in the database."));case 2:if(Hl(a,b,a.Fb)){e.s(4);break}a.sa(Error("Networkless Logging: Stored logs request expired age limit"));if(void 0===b.id){e.s(5);break}return v(e,a.J.ra(b.id,a.v),5);case 5:return e.return();case 4:b.skipRetry||(b=Il(a,b));if(!b){e.s(0);break}if(!b.skipRetry||
void 0===b.id){e.s(8);break}return v(e,a.J.ra(b.id,a.v),8);case 8:a.ea(b.url,b.options,!!b.skipRetry),e.h=0}})}
function Il(a,b){if(!a.v)throw yk("updateRequestHandlers");var c=b.options.onError?b.options.onError:function(){};
b.options.onError=function(e,f){var g;return w(function(h){switch(h.h){case 1:g=Jl(f);if(!(a.C&&a.C("nwl_consider_error_code")&&g||a.C&&!a.C("nwl_consider_error_code")&&a.potentialEsfErrorCounter<=a.Sa)){h.s(2);break}if(!a.I.fa){h.s(3);break}return v(h,a.I.fa(),3);case 3:if(a.I.G()){h.s(2);break}c(e,f);if(!a.C||!a.C("nwl_consider_error_code")||void 0===(null===b||void 0===b?void 0:b.id)){h.s(6);break}return v(h,a.J.jb(b.id,a.v,!1),6);case 6:return h.return();case 2:if(a.C&&a.C("nwl_consider_error_code")&&
!g&&a.potentialEsfErrorCounter>a.Sa)return h.return();a.potentialEsfErrorCounter++;if(void 0===(null===b||void 0===b?void 0:b.id)){h.s(8);break}return b.sendCount<a.Hb?v(h,a.J.jb(b.id,a.v),12):v(h,a.J.ra(b.id,a.v),8);case 12:a.V.M(function(){a.I.G()&&a.Ca()},a.Gb);
case 8:c(e,f),h.h=0}})};
var d=b.options.onSuccess?b.options.onSuccess:function(){};
b.options.onSuccess=function(e,f){return w(function(g){if(1==g.h)return void 0===(null===b||void 0===b?void 0:b.id)?g.s(2):v(g,a.J.ra(b.id,a.v),2);a.I.ca&&a.C&&a.C("vss_network_hint")&&a.I.ca(!0);d(e,f);g.h=0})};
return b}
function Hl(a,b,c){b=b.timestamp;return a.now()-b>=c?!1:!0}
function El(a){if(!a.v)throw yk("retryQueuedRequests");a.J.wb("QUEUED",a.v).then(function(b){b&&!Hl(a,b,a.Db)?a.V.M(function(){return w(function(c){if(1==c.h)return void 0===b.id?c.s(2):v(c,a.J.jb(b.id,a.v),2);El(a);c.h=0})}):a.I.G()&&a.Ca()})}
function Gl(a,b){a.Lb&&!a.I.G()?a.Lb(b):a.handleError(b)}
function Jl(a){var b;return(a=null===(b=null===a||void 0===a?void 0:a.error)||void 0===b?void 0:b.code)&&400<=a&&599>=a?!1:!0}
;function Kl(a,b){this.version=a;this.args=b}
;function Ll(a,b){this.topic=a;this.h=b}
Ll.prototype.toString=function(){return this.topic};var Ml=A("ytPubsub2Pubsub2Instance")||new J;J.prototype.subscribe=J.prototype.subscribe;J.prototype.unsubscribeByKey=J.prototype.wa;J.prototype.publish=J.prototype.ka;J.prototype.clear=J.prototype.clear;z("ytPubsub2Pubsub2Instance",Ml,void 0);var Nl=A("ytPubsub2Pubsub2SubscribedKeys")||{};z("ytPubsub2Pubsub2SubscribedKeys",Nl,void 0);var Ol=A("ytPubsub2Pubsub2TopicToKeys")||{};z("ytPubsub2Pubsub2TopicToKeys",Ol,void 0);var Pl=A("ytPubsub2Pubsub2IsAsync")||{};z("ytPubsub2Pubsub2IsAsync",Pl,void 0);
z("ytPubsub2Pubsub2SkipSubKey",null,void 0);function Ql(a,b){var c=Rl();c&&c.publish.call(c,a.toString(),a,b)}
function Sl(a){var b=Tl,c=Rl();if(!c)return 0;var d=c.subscribe(b.toString(),function(e,f){var g=A("ytPubsub2Pubsub2SkipSubKey");g&&g==d||(g=function(){if(Nl[d])try{if(f&&b instanceof Ll&&b!=e)try{var h=b.h,k=f;if(!k.args||!k.version)throw Error("yt.pubsub2.Data.deserialize(): serializedData is incomplete.");try{if(!h.ja){var m=new h;h.ja=m.version}var n=h.ja}catch(K){}if(!n||k.version!=n)throw Error("yt.pubsub2.Data.deserialize(): serializedData version is incompatible.");try{n=Reflect;var t=n.construct;
var x=k.args,u=x.length;if(0<u){var C=Array(u);for(k=0;k<u;k++)C[k]=x[k];var D=C}else D=[];f=t.call(n,h,D)}catch(K){throw K.message="yt.pubsub2.Data.deserialize(): "+K.message,K;}}catch(K){throw K.message="yt.pubsub2.pubsub2 cross-binary conversion error for "+b.toString()+": "+K.message,K;}a.call(window,f)}catch(K){qh(K)}},Pl[b.toString()]?A("yt.scheduler.instance")?Lh.M(g):Bh(g,0):g())});
Nl[d]=!0;Ol[b.toString()]||(Ol[b.toString()]=[]);Ol[b.toString()].push(d);return d}
function Ul(){var a=Vl,b=Sl(function(c){a.apply(void 0,arguments);Wl(b)});
return b}
function Wl(a){var b=Rl();b&&("number"===typeof a&&(a=[a]),cb(a,function(c){b.unsubscribeByKey(c);delete Nl[c]}))}
function Rl(){return A("ytPubsub2Pubsub2Instance")}
;function Xl(a,b){dl.call(this,a,b);this.options=b;lk(a)}
r(Xl,dl);function Yl(a,b){var c;return function(){c||(c=new Xl(a,b));return c}}
Xl.prototype.j=function(a,b,c){c=void 0===c?{}:c;return(this.options.kb?xl:wl)(a,b,Object.assign({},c))};
Xl.prototype.delete=function(a){a=void 0===a?{}:a;return(this.options.kb?Bl:yl)(this.name,a)};
function Zl(a,b){return Yl(a,b)}
;var $l;
function am(){if($l)return $l();var a={};$l=Zl("LogsDatabaseV2",{za:(a.LogsRequestsStore={Xa:2},a),kb:!1,upgrade:function(b,c,d){c(2)&&Nk(b,"LogsRequestsStore",{keyPath:"id",autoIncrement:!0});c(3);c(5)&&(d=d.objectStore("LogsRequestsStore"),d.h.indexNames.contains("newRequest")&&d.h.deleteIndex("newRequest"),d.h.createIndex("newRequestV2",["status","interface","timestamp"],{unique:!1}));c(7)&&b.h.objectStoreNames.contains("sapisid")&&b.h.deleteObjectStore("sapisid");c(9)&&b.h.objectStoreNames.contains("SWHealthLog")&&b.h.deleteObjectStore("SWHealthLog")},
version:9});return $l()}
;function bm(a){return fl(am(),a)}
function cm(a,b){var c,d,e,f;return w(function(g){if(1==g.h)return c={startTime:M(),transactionType:"YT_IDB_TRANSACTION_TYPE_WRITE"},v(g,bm(b),2);if(3!=g.h)return d=g.i,e=Object.assign(Object.assign({},a),{options:JSON.parse(JSON.stringify(a.options)),interface:B("INNERTUBE_CONTEXT_CLIENT_NAME",0)}),v(g,Pk(d,e),3);f=g.i;c.Ac=M();dm(c);return g.return(f)})}
function em(a,b){var c,d,e,f,g,h,k;return w(function(m){if(1==m.h)return c={startTime:M(),transactionType:"YT_IDB_TRANSACTION_TYPE_READ"},v(m,bm(b),2);if(3!=m.h)return d=m.i,e=B("INNERTUBE_CONTEXT_CLIENT_NAME",0),f=[a,e,0],g=[a,e,M()],h=IDBKeyRange.bound(f,g),k=void 0,v(m,Mk(d,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(n){return Yk(n.objectStore("LogsRequestsStore").index("newRequestV2"),{query:h,direction:"prev"},function(t){t.getValue()&&(k=t.getValue(),"NEW"===a&&(k.status="QUEUED",
t.update(k)))})}),3);
c.Ac=M();dm(c);return m.return(k)})}
function fm(a,b){var c;return w(function(d){if(1==d.h)return v(d,bm(b),2);c=d.i;return d.return(Mk(c,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(e){var f=e.objectStore("LogsRequestsStore");return f.get(a).then(function(g){if(g)return g.status="QUEUED",Jk(f.h.put(g,void 0)).then(function(){return g})})}))})}
function gm(a,b,c){c=void 0===c?!0:c;var d;return w(function(e){if(1==e.h)return v(e,bm(b),2);d=e.i;return e.return(Mk(d,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(f){var g=f.objectStore("LogsRequestsStore");return g.get(a).then(function(h){return h?(h.status="NEW",c&&(h.sendCount+=1),Jk(g.h.put(h,void 0)).then(function(){return h})):Ck.resolve(void 0)})}))})}
function hm(a,b){var c;return w(function(d){if(1==d.h)return v(d,bm(b),2);c=d.i;return d.return(c.delete("LogsRequestsStore",a))})}
function im(a){var b,c;return w(function(d){if(1==d.h)return v(d,bm(a),2);b=d.i;c=M()-2592E6;return v(d,Mk(b,["LogsRequestsStore"],{mode:"readwrite",O:!0},function(e){return Vk(e.objectStore("LogsRequestsStore"),{},function(f){if(f.getValue().timestamp<=c)return f.delete().then(function(){return f.continue()})})}),0)})}
function jm(){return w(function(a){return v(a,Al(),0)})}
function dm(a){L("nwl_csi_killswitch")||.01>=Math.random()&&Ql("nwl_transaction_latency_payload",a)}
;var km={},lm=Zl("ServiceWorkerLogsDatabase",{za:(km.SWHealthLog={Xa:1},km),kb:!0,upgrade:function(a,b){b(1)&&Nk(a,"SWHealthLog",{keyPath:"id",autoIncrement:!0}).h.createIndex("swHealthNewRequest",["interface","timestamp"],{unique:!1})},
version:1});function mm(a){return fl(lm(),a)}
function nm(a){var b,c;return w(function(d){if(1==d.h)return v(d,mm(a),2);b=d.i;c=M()-2592E6;return v(d,Mk(b,["SWHealthLog"],{mode:"readwrite",O:!0},function(e){return Vk(e.objectStore("SWHealthLog"),{},function(f){if(f.getValue().timestamp<=c)return f.delete().then(function(){return f.continue()})})}),0)})}
function om(a){var b;return w(function(c){if(1==c.h)return v(c,mm(a),2);b=c.i;return v(c,b.clear("SWHealthLog"),0)})}
;var pm={},qm=0;
function rm(a){var b=void 0===b?"":b;if(a)if(b)qi(a,void 0,"POST",b,void 0);else if(B("USE_NET_AJAX_FOR_PING_TRANSPORT",!1))qi(a,void 0,"GET","",void 0);else{b:{try{var c=new $a({url:a});if(c.j&&c.i||c.l){var d=Xb(a.match(Wb)[5]||null),e;if(!(e=!d||!d.endsWith("/aclk"))){var f=a.search(cc);d:{for(b=0;0<=(b=a.indexOf("ri",b))&&b<f;){var g=a.charCodeAt(b-1);if(38==g||63==g){var h=a.charCodeAt(b+2);if(!h||61==h||38==h||35==h){var k=b;break d}}b+=3}k=-1}if(0>k)var m=null;else{var n=a.indexOf("&",k);if(0>
n||n>f)n=f;k+=3;m=decodeURIComponent(a.substr(k,n-k).replace(/\+/g," "))}e="1"!==m}var t=!e;break b}}catch(u){}t=!1}if(t){b:{try{if(window.navigator&&window.navigator.sendBeacon&&window.navigator.sendBeacon(a,"")){var x=!0;break b}}catch(u){}x=!1}t=x?!0:!1}else t=!1;t||sm(a)}}
function sm(a){var b=new Image,c=""+qm++;pm[c]=b;b.onload=b.onerror=function(){delete pm[c]};
b.src=a}
;function tm(){this.h=new Map;this.i=!1}
function um(){if(!tm.h){var a=A("yt.networkRequestMonitor.instance")||new tm;z("yt.networkRequestMonitor.instance",a,void 0);tm.h=a}return tm.h}
tm.prototype.requestComplete=function(a,b){b&&(this.i=!0);a=this.removeParams(a);this.h.get(a)||this.h.set(a,b)};
tm.prototype.isEndpointCFR=function(a){a=this.removeParams(a);return(a=this.h.get(a))?!1:!1===a&&this.i?!0:null};
tm.prototype.removeParams=function(a){return a.split("?")[0]};
tm.prototype.removeParams=tm.prototype.removeParams;tm.prototype.isEndpointCFR=tm.prototype.isEndpointCFR;tm.prototype.requestComplete=tm.prototype.requestComplete;tm.getInstance=um;var vm;function wm(){vm||(vm=new Pj("yt.offline"));return vm}
function xm(a){if(L("offline_error_handling")){var b=wm().get("errors",!0)||{};b[a.message]={name:a.name,stack:a.stack};a.level&&(b[a.message].level=a.level);wm().set("errors",b,2592E3,!0)}}
function ym(){if(L("offline_error_handling")){var a=wm().get("errors",!0);if(a){for(var b in a)if(a[b]){var c=new ik(b,"sent via offline_errors");c.name=a[b].name;c.stack=a[b].stack;c.level=a[b].level;qh(c)}wm().set("errors",{},2592E3,!0)}}}
;var zm=gh("network_polling_interval",3E4);function O(){pe.call(this);this.L=0;this.S=this.m=!1;this.l=this.bb();L("use_shared_nsm")?(se.h||(se.h=new se(Lh)),this.j=se.h):(Am(this),Bm(this))}
r(O,pe);function Cm(){if(!O.h){var a=A("yt.networkStatusManager.instance")||new O;z("yt.networkStatusManager.instance",a,void 0);O.h=a}return O.h}
l=O.prototype;l.G=function(){var a;return L("use_shared_nsm")&&this.j?null===(a=this.j)||void 0===a?void 0:a.G():this.l};
l.ca=function(a){var b;L("use_shared_nsm")&&this.j?null===(b=this.j)||void 0===b?void 0:b.j=a:a!==this.l&&(this.l=a)};
l.cc=function(a){!L("use_shared_nsm")&&(this.m=!0,void 0===a?0:a)&&(this.L||Dm(this))};
l.bb=function(){var a=window.navigator.onLine;return void 0===a?!0:a};
l.Tb=function(){this.S=!0};
l.ba=function(a,b){return L("use_shared_nsm")&&this.j?this.j.ba(a,b):pe.prototype.ba.call(this,a,b)};
function Bm(a){window.addEventListener("online",function(){return w(function(b){if(1==b.h)return v(b,a.fa(),2);a.S&&ym();b.h=0})})}
function Am(a){window.addEventListener("offline",function(){return w(function(b){return v(b,a.fa(),0)})})}
function Dm(a){a.L=Fh(function(){return w(function(b){if(1==b.h)return a.l?a.bb()||!a.m?b.s(3):v(b,a.fa(),3):v(b,a.fa(),3);Dm(a);b.h=0})},zm)}
l.fa=function(a){var b=this;if(L("use_shared_nsm")&&this.j){var c=te(this.j,a);c.then(function(d){L("use_cfr_monitor")&&um().requestComplete("generate_204",d)});
return c}return this.u?this.u:this.u=new Promise(function(d){var e,f,g;return w(function(h){switch(h.h){case 1:return e=window.AbortController?new window.AbortController:void 0,f=null===e||void 0===e?void 0:e.signal,g=!1,sa(h,2,3),e&&(b.A=Lh.M(function(){e.abort()},a||2E4)),v(h,fetch("/generate_204",{method:"HEAD",
signal:f}),5);case 5:g=!0;case 3:va(h);L("use_cfr_monitor")&&um().requestComplete("generate_204",g);b.u=void 0;b.A&&Lh.U(b.A);g!==b.l&&(b.l=g,b.l&&b.m?qe(b,"ytnetworkstatus-online"):b.m&&qe(b,"ytnetworkstatus-offline"));d(g);wa(h);break;case 2:ua(h),g=!1,h.s(3)}})})};
O.prototype.sendNetworkCheckRequest=O.prototype.fa;O.prototype.listen=O.prototype.ba;O.prototype.enableErrorFlushing=O.prototype.Tb;O.prototype.getWindowStatus=O.prototype.bb;O.prototype.monitorNetworkStatusChange=O.prototype.cc;O.prototype.networkStatusHint=O.prototype.ca;O.prototype.isNetworkAvailable=O.prototype.G;O.getInstance=Cm;function Em(a){a=void 0===a?{}:a;pe.call(this);var b=this;this.l=this.L=0;this.m="ytnetworkstatus-offline";this.u="ytnetworkstatus-online";L("use_shared_nsm")&&(this.m="networkstatus-offline",this.u="networkstatus-online");this.j=Cm();var c=A("yt.networkStatusManager.instance.monitorNetworkStatusChange").bind(this.j);c&&c(a.tb);a.Pa&&!L("use_shared_nsm")&&(c=A("yt.networkStatusManager.instance.enableErrorFlushing").bind(this.j))&&c();if(c=A("yt.networkStatusManager.instance.listen").bind(this.j))a.Ua?
(this.Ua=a.Ua,c(this.u,function(){Fm(b,"publicytnetworkstatus-online");L("use_shared_nsm")&&a.Pa&&ym()}),c(this.m,function(){Fm(b,"publicytnetworkstatus-offline")})):(c(this.u,function(){qe(b,"publicytnetworkstatus-online");
L("use_shared_nsm")&&a.Pa&&ym()}),c(this.m,function(){qe(b,"publicytnetworkstatus-offline")}))}
r(Em,pe);Em.prototype.G=function(){var a=A("yt.networkStatusManager.instance.isNetworkAvailable");return a?a.bind(this.j)():!0};
Em.prototype.ca=function(a){var b=A("yt.networkStatusManager.instance.networkStatusHint").bind(this.j);b&&b(a)};
Em.prototype.fa=function(a){var b=this,c;return w(function(d){c=A("yt.networkStatusManager.instance.sendNetworkCheckRequest").bind(b.j);return L("skip_network_check_if_cfr")&&um().isEndpointCFR("generate_204")?d.return(new Promise(function(e){var f;b.ca((null===(f=window.navigator)||void 0===f?void 0:f.onLine)||!0);e(b.G())})):c?d.return(c(a)):d.return(!0)})};
function Fm(a,b){a.Ua?a.l?(Lh.U(a.L),a.L=Lh.M(function(){a.A!==b&&(qe(a,b),a.A=b,a.l=M())},a.Ua-(M()-a.l))):(qe(a,b),a.A=b,a.l=M()):qe(a,b)}
;var Gm;function Hm(){Cl.call(this,{J:{Qb:im,ra:hm,wb:em,ac:fm,jb:gm,set:cm},I:Im(),handleError:qh,sa:rh,ea:Jm,now:M,Lb:xm,V:Kh(),ib:"publicytnetworkstatus-online",hb:"publicytnetworkstatus-offline",Ma:!0,La:.1,Sa:gh("potential_esf_error_limit",10),C:L,ya:!jk()});this.j=new Yc;L("networkless_immediately_drop_all_requests")&&jm();Bl("LogsDatabaseV2")}
r(Hm,Cl);function Km(){var a=A("yt.networklessRequestController.instance");a||(a=new Hm,z("yt.networklessRequestController.instance",a,void 0),L("networkless_logging")&&rl().then(function(b){a.v=b;Dl(a);a.j.resolve();a.Ma&&Math.random()<=a.La&&a.v&&nm(a.v);L("networkless_immediately_drop_sw_health_store")&&Lm(a)}));
return a}
Hm.prototype.writeThenSend=function(a,b){b||(b={});jk()||(this.h=!1);Cl.prototype.writeThenSend.call(this,a,b)};
Hm.prototype.sendThenWrite=function(a,b,c){b||(b={});jk()||(this.h=!1);Cl.prototype.sendThenWrite.call(this,a,b,c)};
Hm.prototype.sendAndWrite=function(a,b){b||(b={});jk()||(this.h=!1);Cl.prototype.sendAndWrite.call(this,a,b)};
Hm.prototype.awaitInitialization=function(){return this.j.promise};
function Lm(a){var b;w(function(c){if(!a.v)throw b=yk("clearSWHealthLogsDb"),b;return c.return(om(a.v).catch(function(d){a.handleError(d)}))})}
function Jm(a,b,c){L("use_cfr_monitor")&&Mm(a,b);var d;if(null===(d=b.postParams)||void 0===d?0:d.requestTimeMs)b.postParams.requestTimeMs=Math.round(M());c&&0===Object.keys(b).length?rm(a):ni(a,b)}
function Im(){Gm||(Gm=new Em({Pa:!0,tb:!0}));return Gm}
function Mm(a,b){var c=b.onError?b.onError:function(){};
b.onError=function(e,f){um().requestComplete(a,!1);c(e,f)};
var d=b.onSuccess?b.onSuccess:function(){};
b.onSuccess=function(e,f){um().requestComplete(a,!0);d(e,f)}}
;var Nm=!1,Om=0,Pm=0,Qm,Rm=y.ytNetworklessLoggingInitializationOptions||{isNwlInitialized:Nm,potentialEsfErrorCounter:Pm};z("ytNetworklessLoggingInitializationOptions",Rm,void 0);
function Sm(){var a;w(function(b){switch(b.h){case 1:return v(b,rl(),2);case 2:a=b.i;if(!a||!jk()&&!L("nwl_init_require_datasync_id_killswitch")){b.s(0);break}Nm=!0;Rm.isNwlInitialized=Nm;return v(b,Bl("LogsDatabaseV2"),4);case 4:if(!(.1>=Math.random())){b.s(5);break}return v(b,im(a),6);case 6:return v(b,nm(a),5);case 5:Tm();Um().G()&&Vm();Um().ba("publicytnetworkstatus-online",Vm);Um().ba("publicytnetworkstatus-offline",Wm);if(!L("networkless_immediately_drop_sw_health_store")){b.s(8);break}return v(b,
Xm(),8);case 8:if(L("networkless_immediately_drop_all_requests"))return v(b,jm(),0);b.s(0)}})}
function Ym(a,b){function c(d){var e=Um().G();if(!Zm()||!d||e&&L("vss_networkless_bypass_write"))$m(a,b);else{var f={url:a,options:b,timestamp:M(),status:"NEW",sendCount:0};cm(f,d).then(function(g){f.id=g;Um().G()&&an(f)}).catch(function(g){an(f);
Um().G()?qh(g):xm(g)})}}
b=void 0===b?{}:b;L("skip_is_supported_killswitch")?rl().then(function(d){c(d)}):c(ql())}
function bn(a,b){function c(d){if(Zm()&&d){var e={url:a,options:b,timestamp:M(),status:"NEW",sendCount:0},f=!1,g=b.onSuccess?b.onSuccess:function(){};
e.options.onSuccess=function(k,m){L("use_cfr_monitor")&&um().requestComplete(e.url,!0);void 0!==e.id?hm(e.id,d):f=!0;L("vss_network_hint")&&Um().ca(!0);g(k,m)};
if(L("use_cfr_monitor")){var h=b.onError?b.onError:function(){};
e.options.onError=function(k,m){um().requestComplete(e.url,!1);h(k,m)}}$m(e.url,e.options);
cm(e,d).then(function(k){e.id=k;f&&hm(e.id,d)}).catch(function(k){Um().G()?qh(k):xm(k)})}else $m(a,b)}
b=void 0===b?{}:b;L("skip_is_supported_killswitch")?rl().then(function(d){c(d)}):c(ql())}
function Vm(){var a=ql();if(!a)throw yk("throttleSend");Om||(Om=Lh.M(function(){var b;return w(function(c){if(1==c.h)return v(c,em("NEW",a),2);if(3!=c.h)return b=c.i,b?v(c,an(b),3):(Wm(),c.return());Om&&(Om=0,Vm());c.h=0})},100))}
function Wm(){Lh.U(Om);Om=0}
function an(a){var b,c,d;return w(function(e){switch(e.h){case 1:b=ql();if(!b)throw c=yk("immediateSend"),c;if(void 0===a.id){e.s(2);break}return v(e,fm(a.id,b),3);case 3:(d=e.i)?a=d:rh(Error("The request cannot be found in the database."));case 2:if(cn(a,2592E6)){e.s(4);break}rh(Error("Networkless Logging: Stored logs request expired age limit"));if(void 0===a.id){e.s(5);break}return v(e,hm(a.id,b),5);case 5:return e.return();case 4:a.skipRetry||(a=dn(a));var f=a,g,h;if(null===(h=null===(g=null===
f||void 0===f?void 0:f.options)||void 0===g?void 0:g.postParams)||void 0===h?0:h.requestTimeMs)f.options.postParams.requestTimeMs=Math.round(M());a=f;if(!a){e.s(0);break}if(!a.skipRetry||void 0===a.id){e.s(8);break}return v(e,hm(a.id,b),8);case 8:$m(a.url,a.options,!!a.skipRetry),e.h=0}})}
function dn(a){var b=ql();if(!b)throw yk("updateRequestHandlers");var c=a.options.onError?a.options.onError:function(){};
a.options.onError=function(e,f){var g;return w(function(h){switch(h.h){case 1:L("use_cfr_monitor")&&um().requestComplete(a.url,!1);g=Jl(f);if(!(L("nwl_consider_error_code")&&g||!L("nwl_consider_error_code")&&en()<=gh("potential_esf_error_limit",10))){h.s(2);break}if(L("skip_checking_network_on_cfr_failure")&&(!L("skip_checking_network_on_cfr_failure")||um().isEndpointCFR(a.url))){h.s(3);break}return v(h,Um().fa(),3);case 3:if(Um().G()){h.s(2);break}c(e,f);if(!L("nwl_consider_error_code")||void 0===
(null===a||void 0===a?void 0:a.id)){h.s(6);break}return v(h,gm(a.id,b,!1),6);case 6:return h.return();case 2:if(L("nwl_consider_error_code")&&!g&&en()>gh("potential_esf_error_limit",10))return h.return();A("ytNetworklessLoggingInitializationOptions")&&Rm.potentialEsfErrorCounter++;Pm++;if(void 0===(null===a||void 0===a?void 0:a.id)){h.s(8);break}return 1>a.sendCount?v(h,gm(a.id,b),12):v(h,hm(a.id,b),8);case 12:Lh.M(function(){Um().G()&&Vm()},5E3);
case 8:c(e,f),h.h=0}})};
var d=a.options.onSuccess?a.options.onSuccess:function(){};
a.options.onSuccess=function(e,f){return w(function(g){if(1==g.h)return L("use_cfr_monitor")&&um().requestComplete(a.url,!0),void 0===(null===a||void 0===a?void 0:a.id)?g.s(2):v(g,hm(a.id,b),2);L("vss_network_hint")&&Um().ca(!0);d(e,f);g.h=0})};
return a}
function cn(a,b){a=a.timestamp;return M()-a>=b?!1:!0}
function Tm(){var a=ql();if(!a)throw yk("retryQueuedRequests");em("QUEUED",a).then(function(b){b&&!cn(b,12E4)?Lh.M(function(){return w(function(c){if(1==c.h)return void 0===b.id?c.s(2):v(c,gm(b.id,a),2);Tm();c.h=0})}):Um().G()&&Vm()})}
function Xm(){var a,b;return w(function(c){a=ql();if(!a)throw b=yk("clearSWHealthLogsDb"),b;return c.return(om(a).catch(function(d){qh(d)}))})}
function Um(){if(L("use_new_nwl"))return Im();Qm||(Qm=new Em({Pa:!0,tb:!0}));return Qm}
function $m(a,b,c){c&&0===Object.keys(b).length?rm(a):ni(a,b)}
function Zm(){return A("ytNetworklessLoggingInitializationOptions")?Rm.isNwlInitialized:Nm}
function en(){return A("ytNetworklessLoggingInitializationOptions")?Rm.potentialEsfErrorCounter:Pm}
;function fn(a){var b=this;this.config_=null;a?this.config_=a:Hj()&&(this.config_=bj());Fh(function(){Uj(b)},5E3)}
fn.prototype.isReady=function(){!this.config_&&Hj()&&(this.config_=bj());return!!this.config_};
function fj(a,b,c,d){function e(C){C=void 0===C?!1:C;var D;if(d.retry&&"www.youtube-nocookie.com"!=h&&(C||L("skip_ls_gel_retry")||"application/json"!==g.headers["Content-Type"]||(D=Sj(b,c,m,k)),D)){var K=g.onSuccess,N=g.onFetchSuccess;g.onSuccess=function(S,W){Tj(D);K(S,W)};
c.onFetchSuccess=function(S,W){Tj(D);N(S,W)}}try{C&&d.retry&&!d.Bb.bypassNetworkless?(g.method="POST",d.Bb.writeThenSend?L("use_new_nwl")?Km().writeThenSend(u,g):Ym(u,g):L("use_new_nwl")?Km().sendAndWrite(u,g):bn(u,g)):(g.method="POST",g.postParams||(g.postParams={}),ni(u,g))}catch(S){if("InvalidAccessError"==S.name)D&&(Tj(D),D=0),rh(Error("An extension is blocking network request."));
else throw S;}D&&Fh(function(){Uj(a)},5E3)}
!B("VISITOR_DATA")&&"visitor_id"!==b&&.01>Math.random()&&rh(new ik("Missing VISITOR_DATA when sending innertube request.",b,c,d));if(!a.isReady()){var f=new ik("innertube xhrclient not ready",b,c,d);qh(f);throw f;}var g={headers:d.headers||{},method:"POST",postParams:c,postBody:d.postBody,postBodyFormat:d.postBodyFormat||"JSON",onTimeout:function(){d.onTimeout()},
onFetchTimeout:d.onTimeout,onSuccess:function(C,D){if(d.onSuccess)d.onSuccess(D)},
onFetchSuccess:function(C){if(d.onSuccess)d.onSuccess(C)},
onError:function(C,D){if(d.onError)d.onError(D)},
onFetchError:function(C){if(d.onError)d.onError(C)},
timeout:d.timeout,withCredentials:!0};g.headers["Content-Type"]||(g.headers["Content-Type"]="application/json");var h="";(f=a.config_.Xb)&&(h=f);var k=a.config_.Zb||!1,m=Nj(k,h,d);Object.assign(g.headers,m);(f=g.headers.Authorization)&&!h&&(g.headers["x-origin"]=window.location.origin);var n="/youtubei/"+a.config_.innertubeApiVersion+"/"+b,t={alt:"json"},x=a.config_.Yb&&f;x=x&&f.startsWith("Bearer");x||(t.key=a.config_.innertubeApiKey);var u=bi(""+h+n,t||{},!0);L("use_new_nwl")&&Km().h||!L("use_new_nwl")&&
Zm()?pl().then(function(C){e(C)}):e(!1)}
;function fk(a,b,c){c=void 0===c?{}:c;var d=fn;B("ytLoggingEventsDefaultDisabled",!1)&&fn==fn&&(d=null);mj(a,b,d,c)}
;var gn=[{gb:function(a){return"Cannot read property '"+a.key+"'"},
Ra:{Error:[{regexp:/(Permission denied) to access property "([^']+)"/,groups:["reason","key"]}],TypeError:[{regexp:/Cannot read property '([^']+)' of (null|undefined)/,groups:["key","value"]},{regexp:/\u65e0\u6cd5\u83b7\u53d6\u672a\u5b9a\u4e49\u6216 (null|undefined) \u5f15\u7528\u7684\u5c5e\u6027\u201c([^\u201d]+)\u201d/,groups:["value","key"]},{regexp:/\uc815\uc758\ub418\uc9c0 \uc54a\uc74c \ub610\ub294 (null|undefined) \ucc38\uc870\uc778 '([^']+)' \uc18d\uc131\uc744 \uac00\uc838\uc62c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4./,
groups:["value","key"]},{regexp:/No se puede obtener la propiedad '([^']+)' de referencia nula o sin definir/,groups:["key"]},{regexp:/Unable to get property '([^']+)' of (undefined or null) reference/,groups:["key","value"]},{regexp:/(null) is not an object \(evaluating '(?:([^.]+)\.)?([^']+)'\)/,groups:["value","base","key"]}]}},{gb:function(a){return"Cannot call '"+a.key+"'"},
Ra:{TypeError:[{regexp:/(?:([^ ]+)?\.)?([^ ]+) is not a function/,groups:["base","key"]},{regexp:/([^ ]+) called on (null or undefined)/,groups:["key","value"]},{regexp:/Object (.*) has no method '([^ ]+)'/,groups:["base","key"]},{regexp:/Object doesn't support property or method '([^ ]+)'/,groups:["key"]},{regexp:/\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306f '([^']+)' \u30d7\u30ed\u30d1\u30c6\u30a3\u307e\u305f\u306f\u30e1\u30bd\u30c3\u30c9\u3092\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u307e\u305b\u3093/,
groups:["key"]},{regexp:/\uac1c\uccb4\uac00 '([^']+)' \uc18d\uc131\uc774\ub098 \uba54\uc11c\ub4dc\ub97c \uc9c0\uc6d0\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4./,groups:["key"]}]}},{gb:function(a){return a.key+" is not defined"},
Ra:{ReferenceError:[{regexp:/(.*) is not defined/,groups:["key"]},{regexp:/Can't find variable: (.*)/,groups:["key"]}]}}];var jn={ia:[],ha:[{la:hn,weight:500}]};function hn(a){if("JavaException"===a.name)return!0;a=a.stack;return a.includes("chrome://")||a.includes("chrome-extension://")||a.includes("moz-extension://")}
;function kn(){this.ha=[];this.ia=[]}
var ln;function mn(){if(!ln){var a=ln=new kn;a.ia.length=0;a.ha.length=0;jn.ia&&a.ia.push.apply(a.ia,jn.ia);jn.ha&&a.ha.push.apply(a.ha,jn.ha)}return ln}
;var nn=new J;function on(a){function b(){return a.charCodeAt(d++)}
var c=a.length,d=0;do{var e=pn(b);if(Infinity===e)break;var f=e>>3;switch(e&7){case 0:e=pn(b);if(2===f)return e;break;case 1:if(2===f)return;d+=8;break;case 2:e=pn(b);if(2===f)return a.substr(d,e);d+=e;break;case 5:if(2===f)return;d+=4;break;default:return}}while(d<c)}
function pn(a){var b=a(),c=b&127;if(128>b)return c;b=a();c|=(b&127)<<7;if(128>b)return c;b=a();c|=(b&127)<<14;if(128>b)return c;b=a();return 128>b?c|(b&127)<<21:Infinity}
;function qn(a,b,c,d){if(a)if(Array.isArray(a)){var e=d;for(d=0;d<a.length&&!(a[d]&&(e+=rn(d,a[d],b,c),500<e));d++);d=e}else if("object"===typeof a)for(e in a){if(a[e]){var f=a[e];var g=b;var h=c;g="string"!==typeof f||"clickTrackingParams"!==e&&"trackingParams"!==e?0:(f=on(atob(f.replace(/-/g,"+").replace(/_/g,"/"))))?rn(e+".ve",f,g,h):0;d+=g;d+=rn(e,a[e],b,c);if(500<d)break}}else c[b]=sn(a),d+=c[b].length;else c[b]=sn(a),d+=c[b].length;return d}
function rn(a,b,c,d){c+="."+a;a=sn(b);d[c]=a;return c.length+a.length}
function sn(a){try{return("string"===typeof a?a:String(JSON.stringify(a))).substr(0,500)}catch(b){return"unable to serialize "+typeof a+" ("+b.message+")"}}
;var tn=new Set,un=0,vn=0,wn=0,xn=[],yn=["PhantomJS","Googlebot","TO STOP THIS SECURITY SCAN go/scan"];function ek(a){zn(a)}
function An(a){zn(a,"WARNING")}
function zn(a,b,c,d,e,f){f=void 0===f?{}:f;f.name=c||B("INNERTUBE_CONTEXT_CLIENT_NAME",1);f.version=d||B("INNERTUBE_CONTEXT_CLIENT_VERSION",void 0);c=f||{};b=void 0===b?"ERROR":b;b=void 0===b?"ERROR":b;if(a&&(a.hasOwnProperty("level")&&a.level&&(b=a.level),L("console_log_js_exceptions")&&(d=[],d.push("Name: "+a.name),d.push("Message: "+a.message),a.hasOwnProperty("params")&&d.push("Error Params: "+JSON.stringify(a.params)),a.hasOwnProperty("args")&&d.push("Error args: "+JSON.stringify(a.args)),d.push("File name: "+
a.fileName),d.push("Stacktrace: "+a.stack),window.console.log(d.join("\n"),a)),!(5<=un))){d=xn;var g=Od(a);e=g.message||"Unknown Error";f=g.name||"UnknownError";var h=g.stack||a.i||"Not available";if(h.startsWith(f+": "+e)){var k=h.split("\n");k.shift();h=k.join("\n")}k=g.lineNumber||"Not available";g=g.fileName||"Not available";var m=0;if(a.hasOwnProperty("args")&&a.args&&a.args.length)for(var n=0;n<a.args.length&&!(m=qn(a.args[n],"params."+n,c,m),500<=m);n++);else if(a.hasOwnProperty("params")&&
a.params){var t=a.params;if("object"===typeof a.params)for(n in t){if(t[n]){var x="params."+n,u=sn(t[n]);c[x]=u;m+=x.length+u.length;if(500<m)break}}else c.params=sn(t)}if(d.length)for(n=0;n<d.length&&!(m=qn(d[n],"params.context."+n,c,m),500<=m);n++);navigator.vendor&&!c.hasOwnProperty("vendor")&&(c["device.vendor"]=navigator.vendor);n={message:e,name:f,lineNumber:k,fileName:g,stack:h,params:c,sampleWeight:1};c=Number(a.columnNumber);isNaN(c)||(n.lineNumber=n.lineNumber+":"+c);if("IGNORED"===a.level)a=
0;else a:{a=mn();c=q(a.ia);for(d=c.next();!d.done;d=c.next())if(d=d.value,n.message&&n.message.match(d.xo)){a=d.weight;break a}a=q(a.ha);for(c=a.next();!c.done;c=a.next())if(c=c.value,c.la(n)){a=c.weight;break a}a=1}n.sampleWeight=a;a=q(gn);for(c=a.next();!c.done;c=a.next())if(c=c.value,c.Ra[n.name])for(e=q(c.Ra[n.name]),d=e.next();!d.done;d=e.next())if(f=d.value,d=n.message.match(f.regexp)){n.params["params.error.original"]=d[0];e=f.groups;f={};for(k=0;k<e.length;k++)f[e[k]]=d[k+1],n.params["params.error."+
e[k]]=d[k+1];n.message=c.gb(f);break}n.params||(n.params={});a=mn();n.params["params.errorServiceSignature"]="msg="+a.ia.length+"&cb="+a.ha.length;n.params["params.serviceWorker"]="false";y.document&&y.document.querySelectorAll&&(n.params["params.fscripts"]=String(document.querySelectorAll("script:not([nonce])").length));Ab("sample").constructor!==xb&&(n.params["params.fconst"]="true");window.yterr&&"function"===typeof window.yterr&&window.yterr(n);if(0!==n.sampleWeight&&!tn.has(n.message)){"ERROR"===
b?(nn.ka("handleError",n),L("record_app_crashed_web")&&0===wn&&1===n.sampleWeight&&(wn++,a={appCrashType:"APP_CRASH_TYPE_BREAKPAD"},L("report_client_error_with_app_crash_ks")||(a.systemHealth={crashData:{clientError:{logMessage:{message:n.message}}}}),fk("appCrashed",a)),vn++):"WARNING"===b&&nn.ka("handleWarning",n);if(L("kevlar_gel_error_routing")){a=b;b:{c=q(yn);for(d=c.next();!d.done;d=c.next())if(Wj(d.value.toLowerCase())){c=!0;break b}c=!1}if(c)c=void 0;else{d={stackTrace:n.stack};n.fileName&&
(d.filename=n.fileName);c=n.lineNumber&&n.lineNumber.split?n.lineNumber.split(":"):[];0!==c.length&&(1!==c.length||isNaN(Number(c[0]))?2!==c.length||isNaN(Number(c[0]))||isNaN(Number(c[1]))||(d.lineNumber=Number(c[0]),d.columnNumber=Number(c[1])):d.lineNumber=Number(c[0]));c={level:"ERROR_LEVEL_UNKNOWN",message:n.message,errorClassName:n.name,sampleWeight:n.sampleWeight};"ERROR"===a?c.level="ERROR_LEVEL_ERROR":"WARNING"===a&&(c.level="ERROR_LEVEL_WARNNING");d={isObfuscated:!0,browserStackInfo:d};
e={pageUrl:window.location.href,kvPairs:[]};B("FEXP_EXPERIMENTS")&&(e.experimentIds=B("FEXP_EXPERIMENTS"));f=B("LATEST_ECATCHER_SERVICE_TRACKING_PARAMS",void 0);k=Yg.EXPERIMENT_FLAGS;if((!k||!k.web_disable_gel_stp_ecatcher_killswitch)&&f)for(g=q(Object.keys(f)),k=g.next();!k.done;k=g.next())k=k.value,e.kvPairs.push({key:k,value:String(f[k])});if(f=n.params)for(g=q(Object.keys(f)),k=g.next();!k.done;k=g.next())k=k.value,e.kvPairs.push({key:"client."+k,value:String(f[k])});f=eh("SERVER_NAME");k=eh("SERVER_VERSION");
f&&k&&(e.kvPairs.push({key:"server.name",value:f}),e.kvPairs.push({key:"server.version",value:k}));c={errorMetadata:e,stackTrace:d,logMessage:c}}c&&(fk("clientError",c),("ERROR"===a||L("errors_flush_gel_always_killswitch"))&&Vi())}if(!L("suppress_error_204_logging")){a=n.params||{};b={urlParams:{a:"logerror",t:"jserror",type:n.name,msg:n.message.substr(0,250),line:n.lineNumber,level:b,"client.name":a.name},postParams:{url:B("PAGE_NAME",window.location.href),file:n.fileName},method:"POST"};a.version&&
(b["client.version"]=a.version);if(b.postParams){n.stack&&(b.postParams.stack=n.stack);c=q(Object.keys(a));for(d=c.next();!d.done;d=c.next())d=d.value,b.postParams["client."+d]=a[d];if(a=B("LATEST_ECATCHER_SERVICE_TRACKING_PARAMS",void 0))for(c=q(Object.keys(a)),d=c.next();!d.done;d=c.next())d=d.value,b.postParams[d]=a[d];a=B("SERVER_NAME",void 0);c=B("SERVER_VERSION",void 0);a&&c&&(b.postParams["server.name"]=a,b.postParams["server.version"]=c)}ni(B("ECATCHER_REPORT_HOST","")+"/error_204",b)}try{tn.add(n.message)}catch(C){}un++}}}
function Bn(a){var b=Da.apply(1,arguments),c=a;c.args||(c.args=[]);c.args.push.apply(c.args,ha(b))}
;function Cn(){this.register=new Map}
function Dn(a){a=q(a.register.values());for(var b=a.next();!b.done;b=a.next())b.value.Ao("ABORTED")}
Cn.prototype.clear=function(){Dn(this);this.register.clear()};
var En=new Cn;var Fn=Date.now().toString();
function Gn(){a:{if(window.crypto&&window.crypto.getRandomValues)try{var a=Array(16),b=new Uint8Array(16);window.crypto.getRandomValues(b);for(var c=0;c<a.length;c++)a[c]=b[c];var d=a;break a}catch(e){}d=Array(16);for(a=0;16>a;a++){b=Date.now();for(c=0;c<b%23;c++)d[a]=Math.random();d[a]=Math.floor(256*Math.random())}if(Fn)for(a=1,b=0;b<Fn.length;b++)d[a%16]=d[a%16]^d[(a-1)%16]/4^Fn.charCodeAt(b),a++}a=[];for(b=0;b<d.length;b++)a.push("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".charAt(d[b]&63));
return a.join("")}
;var Hn,In=y.ytLoggingDocDocumentNonce_;In||(In=Gn(),Va("ytLoggingDocDocumentNonce_",In));Hn=In;var Jn={ng:0,nd:1,vd:2,Pj:3,pg:4,Kn:5,Fk:6,em:7,Gl:8,0:"DEFAULT",1:"CHAT",2:"CONVERSATIONS",3:"MINIPLAYER",4:"DIALOG",5:"VOZ",6:"MUSIC_WATCH_TABS",7:"SHARE",8:"PUSH_NOTIFICATIONS"};function Kn(a){this.h=a}
function Ln(a){return new Kn({trackingParams:a})}
Kn.prototype.getAsJson=function(){var a={};void 0!==this.h.trackingParams?a.trackingParams=this.h.trackingParams:(a.veType=this.h.veType,void 0!==this.h.veCounter&&(a.veCounter=this.h.veCounter),void 0!==this.h.elementIndex&&(a.elementIndex=this.h.elementIndex));void 0!==this.h.dataElement&&(a.dataElement=this.h.dataElement.getAsJson());void 0!==this.h.youtubeData&&(a.youtubeData=this.h.youtubeData);return a};
Kn.prototype.getAsJspb=function(){var a=new pg;void 0!==this.h.trackingParams?G(a,1,this.h.trackingParams):(void 0!==this.h.veType&&G(a,2,this.h.veType),void 0!==this.h.veCounter&&G(a,6,this.h.veCounter),void 0!==this.h.elementIndex&&G(a,3,this.h.elementIndex));if(void 0!==this.h.dataElement){var b=this.h.dataElement.getAsJspb();H(a,7,b)}void 0!==this.h.youtubeData&&H(a,8,this.h.jspbYoutubeData);return a};
Kn.prototype.toString=function(){return JSON.stringify(this.getAsJson())};
Kn.prototype.isClientVe=function(){return!this.h.trackingParams&&!!this.h.veType};function Mn(a){a=void 0===a?0:a;return 0==a?"client-screen-nonce":"client-screen-nonce."+a}
function Nn(a){a=void 0===a?0:a;return 0==a?"ROOT_VE_TYPE":"ROOT_VE_TYPE."+a}
function On(a){return B(Nn(void 0===a?0:a),void 0)}
z("yt_logging_screen.getRootVeType",On,void 0);function Pn(a){return(a=On(void 0===a?0:a))?new Kn({veType:a,youtubeData:void 0,jspbYoutubeData:void 0}):null}
function Qn(){var a=B("csn-to-ctt-auth-info");a||(a={},dh("csn-to-ctt-auth-info",a));return a}
function Rn(a){a=void 0===a?0:a;var b=B(Mn(a));if(!b&&!B("USE_CSN_FALLBACK",!0))return null;b||!L("use_undefined_csn_any_layer")&&0!=a||(b="UNDEFINED_CSN");return b?b:null}
z("yt_logging_screen.getCurrentCsn",Rn,void 0);function Sn(a,b,c){var d=Qn();(c=Rn(c))&&delete d[c];b&&(d[a]=b)}
function Tn(a){return Qn()[a]}
z("yt_logging_screen.getCttAuthInfo",Tn,void 0);function Un(a,b,c,d){c=void 0===c?0:c;if(a!==B(Mn(c))||b!==B(Nn(c)))Sn(a,d,c),dh(Mn(c),a),dh(Nn(c),b),b=function(){setTimeout(function(){if(a){var e={clientDocumentNonce:Hn,clientScreenNonce:a};L("use_default_heartbeat_client")?fk("foregroundHeartbeatScreenAssociated",e):mj("foregroundHeartbeatScreenAssociated",e,fn)}},0)},"requestAnimationFrame"in window?window.requestAnimationFrame(b):b()}
z("yt_logging_screen.setCurrentScreen",Un,void 0);var Vn=window.yt&&window.yt.msgs_||window.ytcfg&&window.ytcfg.msgs||{};z("yt.msgs_",Vn,void 0);function Wn(a){Tg(Vn,arguments)}
;var Xn={md:3611,Bc:27686,Cc:85013,Dc:23462,Fc:42016,Gc:62407,Hc:26926,Ec:43781,Ic:51236,Jc:79148,Kc:50160,Lc:77504,Xc:87907,Yc:18630,Zc:54445,bd:80935,cd:105675,dd:37521,ed:47786,fd:98349,gd:123695,hd:6827,jd:29434,kd:7282,ld:124448,pd:32276,od:76278,qd:93911,rd:106531,sd:27259,td:27262,ud:27263,wd:21759,xd:27107,yd:62936,zd:49568,Ad:38408,Bd:80637,Cd:68727,Dd:68728,Ed:80353,Fd:80356,Gd:74610,Hd:45707,Id:83962,Jd:83970,Kd:46713,Ld:89711,Md:74612,Nd:93265,Od:74611,Pd:131380,Rd:128979,Sd:139311,Td:128978,
Qd:131391,Ud:105350,Wd:139312,Xd:134800,Vd:131392,Zd:113533,ae:93252,be:99357,de:94521,ee:114252,ge:113532,he:94522,ce:94583,ie:88E3,je:139580,ke:93253,le:93254,me:94387,ne:94388,oe:93255,pe:97424,Yd:72502,qe:110111,re:76019,te:117092,ue:117093,se:89431,we:110466,xe:77240,ye:60508,ze:137401,Ae:137402,Be:137046,Ce:73393,De:113534,Ee:92098,Fe:131381,Ge:84517,He:83759,Ie:80357,Je:86113,Ke:72598,Le:72733,Me:107349,Ne:124275,Oe:118203,Pe:133275,Qe:133274,Re:133272,Se:133273,Te:133276,Ue:144507,Ve:143247,
We:143248,Xe:143249,Ye:143250,Ze:143251,af:144401,cf:117431,bf:133797,df:128572,ef:133405,ff:117429,gf:117430,hf:117432,jf:120080,kf:117259,lf:121692,mf:132972,nf:133051,pf:133658,qf:132971,rf:97615,tf:143359,sf:143356,vf:143361,uf:143358,xf:143360,wf:143357,yf:142303,zf:143353,Af:143354,Bf:144479,Cf:143355,Df:31402,Ff:133624,Gf:133623,Hf:133622,Ef:133621,If:84774,Jf:95117,Kf:98930,Lf:98931,Mf:98932,Nf:43347,Of:129889,Pf:45474,Qf:100352,Rf:84758,Sf:98443,Tf:117985,Uf:74613,Vf:74614,Wf:64502,Xf:136032,
Yf:74615,Zf:74616,ag:122224,cg:74617,dg:77820,eg:74618,fg:93278,gg:93274,hg:93275,ig:93276,jg:22110,kg:29433,lg:133798,mg:132295,og:120541,qg:82047,rg:113550,sg:75836,tg:75837,ug:42352,vg:84512,wg:76065,xg:75989,yg:16623,zg:32594,Ag:27240,Bg:32633,Cg:74858,Eg:3945,Dg:16989,Fg:45520,Gg:25488,Hg:25492,Ig:25494,Jg:55760,Kg:14057,Lg:18451,Mg:57204,Ng:57203,Og:17897,Pg:57205,Qg:18198,Rg:17898,Sg:17909,Tg:43980,Ug:46220,Vg:11721,Wg:49954,Xg:96369,Yg:3854,Zg:56251,ah:25624,sh:16906,th:99999,uh:68172,vh:27068,
wh:47973,xh:72773,yh:26970,zh:26971,Ah:96805,Bh:17752,Ch:73233,Dh:109512,Eh:22256,Fh:14115,Gh:22696,Hh:89278,Ih:89277,Jh:109513,Kh:43278,Lh:43459,Mh:43464,Nh:89279,Oh:43717,Ph:55764,Qh:22255,Rh:89281,Sh:40963,Th:43277,Uh:43442,Vh:91824,Wh:120137,Xh:96367,Yh:36850,Zh:72694,ai:37414,bi:36851,di:124863,ci:121343,fi:73491,gi:54473,hi:43375,ii:46674,ji:143815,ki:139095,li:144402,mi:32473,ni:72901,oi:72906,ri:50947,si:50612,ti:50613,vi:50942,wi:84938,xi:84943,yi:84939,zi:84941,Ai:84944,Bi:84940,Ci:84942,
Di:35585,Ei:51926,Fi:79983,Gi:63238,Hi:18921,Ii:63241,Ji:57893,Ki:41182,Li:135732,Mi:33424,Ni:22207,Oi:42993,Pi:36229,Qi:22206,Ri:22205,Si:18993,Ti:19001,Ui:18990,Vi:18991,Wi:18997,Xi:18725,Yi:19003,Zi:36874,aj:44763,bj:33427,cj:67793,dj:22182,ej:37091,fj:34650,gj:50617,hj:47261,ij:22287,jj:25144,kj:97917,lj:62397,mj:125598,nj:137935,oj:36961,pj:108035,qj:27426,rj:27857,sj:27846,tj:27854,uj:69692,vj:61411,wj:39299,xj:38696,yj:62520,zj:36382,Aj:108701,Bj:50663,Cj:36387,Dj:14908,Ej:37533,Fj:105443,
Gj:61635,Hj:62274,Ij:133818,Jj:65702,Kj:65703,Lj:65701,Mj:76256,Nj:37671,Oj:49953,Qj:36216,Rj:28237,Sj:39553,Tj:29222,Uj:26107,Vj:38050,Wj:26108,Yj:120745,Xj:26109,Zj:26110,ak:66881,bk:28236,ck:14586,dk:57929,ek:74723,fk:44098,gk:44099,jk:23528,kk:61699,hk:134104,ik:134103,lk:59149,mk:101951,nk:97346,pk:118051,qk:95102,rk:64882,sk:119505,tk:63595,uk:63349,vk:95101,wk:75240,xk:27039,yk:68823,zk:21537,Ak:83464,Bk:75707,Ck:83113,Dk:101952,Ek:101953,Gk:79610,Hk:125755,Ik:24402,Jk:24400,Kk:32925,Lk:57173,
Mk:122502,Nk:138480,Ok:64423,Pk:64424,Qk:33986,Rk:100828,Sk:129089,Tk:21409,Xk:135155,Yk:135156,Zk:135157,al:135158,bl:135159,dl:135160,fl:135161,il:135162,jl:135163,kl:135164,ll:135165,ml:135166,Uk:11070,Vk:11074,Wk:17880,nl:14001,pl:30709,ql:30707,rl:30711,sl:30710,ul:30708,ol:26984,vl:63648,wl:63649,xl:51879,yl:111059,zl:5754,Al:20445,Cl:130975,Bl:130976,Dl:110386,El:113746,Fl:66557,Hl:17310,Il:28631,Jl:21589,Kl:68012,Ll:60480,Ml:138664,Nl:141121,Ol:31571,Pl:141978,Ql:76980,Rl:41577,Sl:45469,Tl:38669,
Ul:13768,Vl:13777,Wl:141842,Xl:62985,Yl:4724,Zl:59369,am:43927,bm:43928,cm:12924,dm:100355,gm:56219,hm:27669,im:10337,fm:47896,jm:122629,lm:139723,km:139722,mm:121258,nm:107598,om:127991,pm:96639,qm:107536,rm:130169,sm:96661,tm:96658,um:116646,vm:121122,wm:96660,xm:127738,ym:127083,zm:104443,Am:96659,Bm:106442,Cm:134840,Dm:63667,Em:63668,Fm:63669,Gm:130686,Hm:78314,Im:55761,Jm:127098,Km:134841,Lm:96368,Mm:67374,Nm:48992,Om:49956,Pm:31961,Qm:26388,Rm:23811,Sm:5E4,Tm:126250,Um:96370,Vm:47355,Wm:47356,
Xm:37935,Ym:45521,Zm:21760,an:83769,bn:49977,cn:49974,dn:93497,en:93498,fn:34325,gn:140759,hn:115803,jn:123707,kn:100081,ln:35309,mn:68314,nn:25602,pn:100339,qn:143516,rn:59018,sn:18248,tn:50625,un:9729,vn:37168,wn:37169,xn:21667,yn:16749,zn:18635,An:39305,Bn:18046,Cn:53969,Dn:8213,En:93926,Fn:102852,Gn:110099,Hn:22678,In:69076,Jn:137575,Ln:139224,Mn:100856,Nn:17736,On:3832,Pn:55759,Qn:64031,Wn:93044,Xn:93045,Yn:34388,Zn:17657,ao:17655,bo:39579,co:39578,eo:77448,fo:8196,ho:11357,jo:69877,ko:8197,
lo:82039};function Yn(){var a=rb(Zn),b;return Af(new tf(function(c,d){a.onSuccess=function(e){hi(e)?c(new $n(e)):d(new ao("Request failed, status="+(e&&"status"in e?e.status:-1),"net.badstatus",e))};
a.onError=function(e){d(new ao("Unknown request error","net.unknown",e))};
a.onTimeout=function(e){d(new ao("Request timed out","net.timeout",e))};
b=ni("//googleads.g.doubleclick.net/pagead/id",a)}),function(c){c instanceof Bf&&b.abort();
return yf(c)})}
function ao(a,b,c){Za.call(this,a+", errorCode="+b);this.errorCode=b;this.xhr=c;this.name="PromiseAjaxError"}
r(ao,Za);function $n(a){this.xhr=a}
;function bo(){this.i=0;this.h=null}
bo.prototype.then=function(a,b,c){return 1===this.i&&a?(a=a.call(c,this.h),sf(a)?a:co(a)):2===this.i&&b?(a=b.call(c,this.h),sf(a)?a:eo(a)):this};
bo.prototype.getValue=function(){return this.h};
bo.prototype.$goog_Thenable=!0;function eo(a){var b=new bo;a=void 0===a?null:a;b.i=2;b.h=void 0===a?null:a;return b}
function co(a){var b=new bo;a=void 0===a?null:a;b.i=1;b.h=void 0===a?null:a;return b}
;function fo(){if(Hd())return!0;var a=B("INNERTUBE_CLIENT_NAME");return!a||"WEB"!==a&&"MWEB"!==a||Vj&&Wj("applewebkit")&&!Wj("version")&&(!Wj("safari")||Wj("gsa/"))||kc&&Wj("version/")?!0:(a=sj("CONSENT"))?a.startsWith("YES+"):!0}
;function go(a){Za.call(this,a.message||a.description||a.name);this.isMissing=a instanceof ho;this.isTimeout=a instanceof ao&&"net.timeout"==a.errorCode;this.isCanceled=a instanceof Bf}
r(go,Za);go.prototype.name="BiscottiError";function ho(){Za.call(this,"Biscotti ID is missing from server")}
r(ho,Za);ho.prototype.name="BiscottiMissingError";var Zn={format:"RAW",method:"GET",timeout:5E3,withCredentials:!0},io=null;function Sh(){if(L("disable_biscotti_fetch_entirely_for_all_web_clients"))return yf(Error("Biscotti id fetching has been disabled entirely."));if(!fo())return yf(Error("User has not consented - not fetching biscotti id."));if("1"==pb())return yf(Error("Biscotti ID is not available in private embed mode"));io||(io=Af(Yn().then(jo),function(a){return ko(2,a)}));
return io}
function jo(a){a=a.xhr.responseText;if(0!=a.lastIndexOf(")]}'",0))throw new ho;a=JSON.parse(a.substr(4));if(1<(a.type||1))throw new ho;a=a.id;Th(a);io=co(a);lo(18E5,2);return a}
function ko(a,b){b=new go(b);Th("");io=eo(b);0<a&&lo(12E4,a-1);throw b;}
function lo(a,b){Bh(function(){Af(Yn().then(jo,function(c){return ko(b,c)}),Ia)},a)}
function mo(){try{var a=A("yt.ads.biscotti.getId_");return a?a():Sh()}catch(b){return yf(b)}}
;function no(a){if("1"!=pb()){a&&Rh();try{mo().then(function(){},function(){}),Bh(no,18E5)}catch(b){qh(b)}}}
;function oo(){this.yc=!0}
function po(a){var b={},c=Jd([]);c&&(b.Authorization=c,c=a=null===a||void 0===a?void 0:a.sessionIndex,void 0===c&&(c=Number(B("SESSION_INDEX",0)),c=isNaN(c)?0:c),b["X-Goog-AuthUser"]=c,"INNERTUBE_HOST_OVERRIDE"in Yg||(b["X-Origin"]=window.location.origin),void 0===a&&"DELEGATED_SESSION_ID"in Yg&&(b["X-Goog-PageId"]=B("DELEGATED_SESSION_ID")));return b}
;var qo={identityType:"UNAUTHENTICATED_IDENTITY_TYPE_UNKNOWN"};var ro=new Map([["dark","USER_INTERFACE_THEME_DARK"],["light","USER_INTERFACE_THEME_LIGHT"]]),so=["/fashion","/feed/fashion_destination","/channel/UCrpQ4p1Ql_hG8rKXIKM1MOQ"];function to(){var a=void 0===a?window.location.href:a;if(L("kevlar_disable_theme_param"))return null;var b=Xb(a.match(Wb)[5]||null);if(uo(b))return"USER_INTERFACE_THEME_DARK";try{var c=ai(a).theme;return ro.get(c)||null}catch(d){}return null}
function uo(a){var b=so.map(function(c){return c.toLowerCase()});
return!L("disable_dark_fashion_destination_launch")&&b.some(function(c){return a.toLowerCase().startsWith(c)})?!0:!1}
;function vo(){this.h={};if(this.i=tj()){var a=sj("CONSISTENCY");a&&wo(this,{encryptedTokenJarContents:a})}}
vo.prototype.handleResponse=function(a,b){var c,d,e;b=(null===(d=null===(c=b.aa.context)||void 0===c?void 0:c.request)||void 0===d?void 0:d.consistencyTokenJars)||[];(a=null===(e=a.responseContext)||void 0===e?void 0:e.consistencyTokenJar)&&this.replace(b,a)};
vo.prototype.replace=function(a,b){a=q(a);for(var c=a.next();!c.done;c=a.next())delete this.h[c.value.encryptedTokenJarContents];wo(this,b)};
function wo(a,b){if(b.encryptedTokenJarContents&&(a.h[b.encryptedTokenJarContents]=b,"string"===typeof b.expirationSeconds)){var c=Number(b.expirationSeconds);setTimeout(function(){delete a.h[b.encryptedTokenJarContents]},1E3*c);
a.i&&rj("CONSISTENCY",b.encryptedTokenJarContents,c,void 0,!0)}}
;var xo=window.location.hostname.split(".").slice(-2).join(".");function yo(){var a=B("LOCATION_PLAYABILITY_TOKEN");"TVHTML5"===B("INNERTUBE_CLIENT_NAME")&&(this.h=zo(this))&&(a=this.h.get("yt-location-playability-token"));a&&(this.locationPlayabilityToken=a,this.i=void 0)}
var Ao;yo.getInstance=function(){Ao=A("yt.clientLocationService.instance");Ao||(Ao=new yo,z("yt.clientLocationService.instance",Ao,void 0));return Ao};
yo.prototype.setLocationOnInnerTubeContext=function(a){a.client||(a.client={});this.i?(a.client.locationInfo||(a.client.locationInfo={}),a.client.locationInfo.latitudeE7=1E7*this.i.coords.latitude,a.client.locationInfo.longitudeE7=1E7*this.i.coords.longitude,a.client.locationInfo.horizontalAccuracyMeters=this.i.coords.accuracy,a.client.locationInfo.forceLocationPlayabilityTokenRefresh=!0):this.locationPlayabilityToken&&(a.client.locationPlayabilityToken=this.locationPlayabilityToken)};
yo.prototype.handleResponse=function(a){var b;a=null===(b=a.responseContext)||void 0===b?void 0:b.locationPlayabilityToken;void 0!==a&&(this.locationPlayabilityToken=a,this.i=void 0,"TVHTML5"===B("INNERTUBE_CLIENT_NAME")?(this.h=zo(this))&&this.h.set("yt-location-playability-token",a,15552E3):rj("YT_CL",JSON.stringify({vo:a}),15552E3,xo,!0))};
function zo(a){return void 0===a.h?new Pj("yt-client-location"):a.h}
yo.prototype.getCurrentPositionFromGeolocation=function(){var a=this;if(!(navigator&&navigator.geolocation&&navigator.geolocation.getCurrentPosition)||!L("web_enable_browser_geolocation_api")&&!L("enable_handoff_location_2fa_on_mweb"))return Promise.reject(Error("Geolocation unsupported"));var b=!1,c=1E4;L("enable_handoff_location_2fa_on_mweb")&&(b=!0,c=15E3);return new Promise(function(d,e){navigator.geolocation.getCurrentPosition(function(f){a.i=f;d(f)},function(f){e(f)},{enableHighAccuracy:b,
maximumAge:0,timeout:c})})};
yo.prototype.createUnpluggedLocationInfo=function(a){var b={};a=a.coords;if(null===a||void 0===a?0:a.latitude)b.latitudeE7=Math.floor(1E7*a.latitude);if(null===a||void 0===a?0:a.longitude)b.longitudeE7=Math.floor(1E7*a.longitude);if(null===a||void 0===a?0:a.accuracy)b.locationRadiusMeters=Math.round(a.accuracy);return b};function Bo(a,b){var c,d;if((null===(c=a.signalServiceEndpoint)||void 0===c?0:c.signal)&&b.Ba){var e=b.Ba[a.signalServiceEndpoint.signal];if(e)return e()}if((null===(d=a.continuationCommand)||void 0===d?0:d.request)&&b.Rb&&(e=b.Rb[a.continuationCommand.request]))return e();for(var f in a)if(b.pb[f]&&(a=b.pb[f]))return a()}
;function Co(a){return function(){return new a}}
;var Do={},Eo=(Do.WEB_UNPLUGGED="^unplugged/",Do.WEB_UNPLUGGED_ONBOARDING="^unplugged/",Do.WEB_UNPLUGGED_OPS="^unplugged/",Do.WEB_UNPLUGGED_PUBLIC="^unplugged/",Do.WEB_CREATOR="^creator/",Do.WEB_KIDS="^kids/",Do.WEB_EXPERIMENTS="^experiments/",Do.WEB_MUSIC="^music/",Do.WEB_REMIX="^music/",Do.WEB_MUSIC_EMBEDDED_PLAYER="^music/",Do.WEB_MUSIC_EMBEDDED_PLAYER="^main_app/|^sfv/",Do);
function Fo(a){var b=void 0===b?"UNKNOWN_INTERFACE":b;if(1===a.length)return a[0];var c=Eo[b];if(c){var d=new RegExp(c),e=q(a);for(c=e.next();!c.done;c=e.next())if(c=c.value,d.exec(c))return c}var f=[];Object.entries(Eo).forEach(function(g){var h=q(g);g=h.next().value;h=h.next().value;b!==g&&f.push(h)});
d=new RegExp(f.join("|"));a.sort(function(g,h){return g.length-h.length});
e=q(a);for(c=e.next();!c.done;c=e.next())if(c=c.value,!d.exec(c))return c;return a[0]}
;function Go(a,b){return{method:void 0===b?"POST":b,mode:ci(a)?"same-origin":"cors",credentials:ci(a)?"same-origin":"include"}}
;function Ho(){}
Ho.prototype.o=function(a,b,c){b=void 0===b?{}:b;c=void 0===c?qo:c;var d;var e=a.clickTrackingParams,f=this.l,g=!1;g=void 0===g?!1:g;f=void 0===f?!1:f;var h=B("INNERTUBE_CONTEXT");if(h){h=sb(h);L("web_no_tracking_params_in_shell_killswitch")||delete h.clickTracking;var k,m;h.client||(h.client={});var n=h.client;"MWEB"===n.clientName&&(n.clientFormFactor=B("IS_TABLET")?"LARGE_FORM_FACTOR":"SMALL_FORM_FACTOR");n.screenWidthPoints=window.innerWidth;n.screenHeightPoints=window.innerHeight;n.screenPixelDensity=
Math.round(window.devicePixelRatio||1);n.screenDensityFloat=window.devicePixelRatio||1;n.utcOffsetMinutes=-Math.floor((new Date).getTimezoneOffset());var t=void 0===t?!1:t;vj.getInstance();var x="USER_INTERFACE_THEME_LIGHT";yj(165)?x="USER_INTERFACE_THEME_DARK":yj(174)?x="USER_INTERFACE_THEME_LIGHT":!L("kevlar_legacy_browsers")&&window.matchMedia&&window.matchMedia("(prefers-color-scheme)").matches&&window.matchMedia("(prefers-color-scheme: dark)").matches&&(x="USER_INTERFACE_THEME_DARK");t=t?x:to()||
x;n.userInterfaceTheme=t;if(!g){if(t=Fj())n.connectionType=t;L("web_log_effective_connection_type")&&(t=Gj())&&(h.client.effectiveConnectionType=t)}L("web_log_memory_total_kbytes")&&(null===(k=y.navigator)||void 0===k?0:k.deviceMemory)&&(k=null===(m=y.navigator)||void 0===m?void 0:m.deviceMemory,h.client.memoryTotalKbytes=""+1E6*k);m=ai(y.location.href);!L("web_populate_internal_geo_killswitch")&&m.internalcountrycode&&(n.internalGeo=m.internalcountrycode);"MWEB"===n.clientName||"WEB"===n.clientName?
(n.mainAppWebInfo={graftUrl:y.location.href},L("kevlar_woffle")&&oj.h&&(n.mainAppWebInfo.pwaInstallabilityStatus=oj.h.h?"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED":"PWA_INSTALLABILITY_STATUS_UNKNOWN"),n.mainAppWebInfo.webDisplayMode=pj(),n.mainAppWebInfo.isWebNativeShareAvailable=navigator&&void 0!==navigator.share):"TVHTML5"===n.clientName&&(!L("web_lr_app_quality_killswitch")&&(m=B("LIVING_ROOM_APP_QUALITY"))&&(n.tvAppInfo=Object.assign(n.tvAppInfo||{},{appQuality:m})),m=B("LIVING_ROOM_CERTIFICATION_SCOPE"))&&
(n.tvAppInfo=Object.assign(n.tvAppInfo||{},{certificationScope:m}));if(!L("web_populate_time_zone_itc_killswitch")){b:{if("undefined"!==typeof Intl)try{var u=(new Intl.DateTimeFormat).resolvedOptions().timeZone;break b}catch(fa){}u=void 0}u&&(n.timeZone=u)}(u=hh())?n.experimentsToken=u:delete n.experimentsToken;u=ih();vo.h||(vo.h=new vo);n=vo.h.h;m=[];k=0;for(var C in n)m[k++]=n[C];h.request=Object.assign(Object.assign({},h.request),{internalExperimentFlags:u,consistencyTokenJars:m});!L("web_prequest_context_killswitch")&&
(C=B("INNERTUBE_CONTEXT_PREQUEST_CONTEXT"))&&(h.request.externalPrequestContext=C);u=vj.getInstance();C=yj(58);u=u.get("gsml","");h.user=Object.assign({},h.user);C&&(h.user.enableSafetyMode=C);u&&(h.user.lockedSafetyMode=!0);L("warm_op_csn_cleanup")?f&&(g=Rn())&&(h.clientScreenNonce=g):!g&&(g=Rn())&&(h.clientScreenNonce=g);e&&(h.clickTracking={clickTrackingParams:e});if(e=A("yt.mdx.remote.remoteClient_"))h.remoteClient=e;L("web_enable_client_location_service")&&yo.getInstance().setLocationOnInnerTubeContext(h);
try{var D=di(void 0),K=D.bid;delete D.bid;h.adSignalsInfo={params:[],bid:K};for(var N=q(Object.entries(D)),S=N.next();!S.done;S=N.next()){var W=q(S.value),Qa=W.next().value,zb=W.next().value;D=Qa;K=zb;null===(d=h.adSignalsInfo.params)||void 0===d?void 0:d.push({key:D,value:""+K})}}catch(fa){zn(fa)}d=h}else zn(Error("Error: No InnerTubeContext shell provided in ytconfig.")),d={};d={context:d};if(N=this.h(a)){this.i(d,N,b);var P,Y;b="/youtubei/v1/"+Fo(this.j());(a=null===(Y=null===(P=a.commandMetadata)||
void 0===P?void 0:P.webCommandMetadata)||void 0===Y?void 0:Y.apiUrl)&&(b=a);P=b;(Y=B("INNERTUBE_HOST_OVERRIDE"))&&(P=String(Y)+String(Zb(P)));Y={};Y.key=B("INNERTUBE_API_KEY");L("json_condensed_response")&&(Y.prettyPrint="false");P=bi(P,Y||{},!1);P={input:P,ta:Go(P),aa:d,config:Object.assign({},void 0)};P.config.Ia?P.config.Ia.identity=c:P.config.Ia={identity:c};return P}zn(new ik("Error: Failed to create Request from Command.",a))};
da.Object.defineProperties(Ho.prototype,{l:{configurable:!0,enumerable:!0,get:function(){return!1}}});function Io(){}
r(Io,Ho);Io.prototype.o=function(){return{input:"/getDatasyncIdsEndpoint",ta:Go("/getDatasyncIdsEndpoint","GET"),aa:{}}};
Io.prototype.j=function(){return[]};
Io.prototype.h=function(){};
Io.prototype.i=function(){};var Jo={},Ko=(Jo.GET_DATASYNC_IDS=Co(Io),Jo);function Lo(a){var b=Da.apply(1,arguments);if(!Mo(a)||b.some(function(e){return!Mo(e)}))throw Error("Only objects may be merged.");
var c=a;b=q(b);for(var d=b.next();!d.done;d=b.next())No(c,d.value);return c}
function No(a,b){for(var c in b)if(Mo(b[c])){if(c in a&&!Mo(a[c]))throw Error("Cannot merge an object into a non-object.");c in a||(a[c]={});No(a[c],b[c])}else if(Oo(b[c])){if(c in a&&!Oo(a[c]))throw Error("Cannot merge an array into a non-array.");c in a||(a[c]=[]);Po(a[c],b[c])}else a[c]=b[c];return a}
function Po(a,b){b=q(b);for(var c=b.next();!c.done;c=b.next())c=c.value,Mo(c)?a.push(No({},c)):Oo(c)?a.push(Po([],c)):a.push(c);return a}
function Mo(a){return"object"===typeof a&&!Array.isArray(a)}
function Oo(a){return"object"===typeof a&&Array.isArray(a)}
;function Qo(a,b){Kl.call(this,1,arguments);this.timer=b}
r(Qo,Kl);var Ro=new Ll("aft-recorded",Qo);var So=window;function To(){this.timing={};this.clearResourceTimings=function(){};
this.webkitClearResourceTimings=function(){};
this.mozClearResourceTimings=function(){};
this.msClearResourceTimings=function(){};
this.oClearResourceTimings=function(){}}
var Q=So.performance||So.mozPerformance||So.msPerformance||So.webkitPerformance||new To;var Uo=!1,Vo={'script[name="scheduler/scheduler"]':"sj",'script[name="player/base"]':"pj",'link[rel="stylesheet"][name="www-player"]':"pc",'link[rel="stylesheet"][name="player/www-player"]':"pc",'script[name="desktop_polymer/desktop_polymer"]':"dpj",'link[rel="import"][name="desktop_polymer"]':"dph",'script[name="mobile-c3"]':"mcj",'link[rel="stylesheet"][name="mobile-c3"]':"mcc",'script[name="player-plasma-ias-phone/base"]':"mcppj",'script[name="player-plasma-ias-tablet/base"]':"mcptj",'link[rel="stylesheet"][name="mobile-polymer-player-ias"]':"mcpc",
'link[rel="stylesheet"][name="mobile-polymer-player-svg-ias"]':"mcpsc",'script[name="mobile_blazer_core_mod"]':"mbcj",'link[rel="stylesheet"][name="mobile_blazer_css"]':"mbc",'script[name="mobile_blazer_logged_in_users_mod"]':"mbliuj",'script[name="mobile_blazer_logged_out_users_mod"]':"mblouj",'script[name="mobile_blazer_noncore_mod"]':"mbnj","#player_css":"mbpc",'script[name="mobile_blazer_desktopplayer_mod"]':"mbpj",'link[rel="stylesheet"][name="mobile_blazer_tablet_css"]':"mbtc",'script[name="mobile_blazer_watch_mod"]':"mbwj"},
Wo=Ta(Q.clearResourceTimings||Q.webkitClearResourceTimings||Q.mozClearResourceTimings||Q.msClearResourceTimings||Q.oClearResourceTimings||Ia,Q);function Xo(a){var b=Yo(a);if(b.aft)return b.aft;a=B((a||"")+"TIMING_AFT_KEYS",["ol"]);for(var c=a.length,d=0;d<c;d++){var e=b[a[d]];if(e)return e}return NaN}
function Zo(){var a;if(L("csi_use_performance_navigation_timing")){var b,c,d,e=null===(d=null===(c=null===(b=null===(a=null===Q||void 0===Q?void 0:Q.getEntriesByType)||void 0===a?void 0:a.call(Q,"navigation"))||void 0===b?void 0:b[0])||void 0===c?void 0:c.toJSON)||void 0===d?void 0:d.call(c);e?(e.requestStart=$o(e.requestStart),e.responseEnd=$o(e.responseEnd),e.redirectStart=$o(e.redirectStart),e.redirectEnd=$o(e.redirectEnd),e.domainLookupEnd=$o(e.domainLookupEnd),e.connectStart=$o(e.connectStart),
e.connectEnd=$o(e.connectEnd),e.responseStart=$o(e.responseStart),e.secureConnectionStart=$o(e.secureConnectionStart),e.domainLookupStart=$o(e.domainLookupStart),e.isPerformanceNavigationTiming=!0,a=e):a=Q.timing}else a=Q.timing;return a}
function ap(){return L("csi_use_time_origin")&&Q.timeOrigin?Math.floor(Q.timeOrigin):Q.timing.navigationStart}
function $o(a){return Math.round(ap()+a)}
function bp(a){z("ytglobal.timing"+(a||"")+"ready_",!0,void 0)}
function cp(a){return A("ytcsi."+(a||"")+"data_")||dp(a)}
function ep(a){a=cp(a);a.info||(a.info={});return a.info}
function Yo(a){a=cp(a);a.tick||(a.tick={});return a.tick}
function fp(a){a=cp(a);if(a.gel){var b=a.gel;b.gelInfos||(b.gelInfos={});b.gelTicks||(b.gelTicks={})}else a.gel={gelTicks:{},gelInfos:{}};return a.gel}
function gp(a){a=fp(a);a.gelInfos||(a.gelInfos={});return a.gelInfos}
function hp(a){var b=cp(a).nonce;b||(b=Gn(),cp(a).nonce=b);return b}
function dp(a){var b={tick:{},info:{}};z("ytcsi."+(a||"")+"data_",b,void 0);return b}
function ip(a){var b=Yo(a||""),c=Xo(a);c&&!Uo&&(Ql(Ro,new Qo(Math.round(c-b._start),a)),Uo=!0)}
function jp(a,b){for(var c=q(Object.keys(b)),d=c.next();!d.done;d=c.next())if(d=d.value,!Object.keys(a).includes(d)||"object"===typeof b[d]&&!jp(a[d],b[d]))return!1;return!0}
;function kp(){if(Q.getEntriesByType){var a=Q.getEntriesByType("paint");if(a=gb(a,function(b){return"first-paint"===b.name}))return $o(a.startTime)}a=Q.timing;
return a.dc?Math.max(0,a.dc):0}
;function lp(){var a=A("ytcsi.debug");a||(a=[],z("ytcsi.debug",a,void 0),z("ytcsi.reference",{},void 0));return a}
function mp(a){a=a||"";var b=np();if(b[a])return b[a];var c=lp(),d={timerName:a,info:{},tick:{},span:{},jspbInfo:[]};c.push(d);return b[a]=d}
function np(){var a=A("ytcsi.reference");if(a)return a;lp();return A("ytcsi.reference")}
;var R={},op=(R.auto_search="LATENCY_ACTION_AUTO_SEARCH",R.ad_to_ad="LATENCY_ACTION_AD_TO_AD",R.ad_to_video="LATENCY_ACTION_AD_TO_VIDEO",R["analytics.explore"]="LATENCY_ACTION_CREATOR_ANALYTICS_EXPLORE",R.app_startup="LATENCY_ACTION_APP_STARTUP",R["artist.analytics"]="LATENCY_ACTION_CREATOR_ARTIST_ANALYTICS",R["artist.events"]="LATENCY_ACTION_CREATOR_ARTIST_CONCERTS",R["artist.presskit"]="LATENCY_ACTION_CREATOR_ARTIST_PROFILE",R.browse="LATENCY_ACTION_BROWSE",R.cast_splash="LATENCY_ACTION_CAST_SPLASH",
R.channels="LATENCY_ACTION_CHANNELS",R.creator_channel_dashboard="LATENCY_ACTION_CREATOR_CHANNEL_DASHBOARD",R["channel.analytics"]="LATENCY_ACTION_CREATOR_CHANNEL_ANALYTICS",R["channel.comments"]="LATENCY_ACTION_CREATOR_CHANNEL_COMMENTS",R["channel.content"]="LATENCY_ACTION_CREATOR_POST_LIST",R["channel.copyright"]="LATENCY_ACTION_CREATOR_CHANNEL_COPYRIGHT",R["channel.editing"]="LATENCY_ACTION_CREATOR_CHANNEL_EDITING",R["channel.monetization"]="LATENCY_ACTION_CREATOR_CHANNEL_MONETIZATION",R["channel.music"]=
"LATENCY_ACTION_CREATOR_CHANNEL_MUSIC",R["channel.playlists"]="LATENCY_ACTION_CREATOR_CHANNEL_PLAYLISTS",R["channel.translations"]="LATENCY_ACTION_CREATOR_CHANNEL_TRANSLATIONS",R["channel.videos"]="LATENCY_ACTION_CREATOR_CHANNEL_VIDEOS",R["channel.live_streaming"]="LATENCY_ACTION_CREATOR_LIVE_STREAMING",R.chips="LATENCY_ACTION_CHIPS",R["dialog.copyright_strikes"]="LATENCY_ACTION_CREATOR_DIALOG_COPYRIGHT_STRIKES",R["dialog.uploads"]="LATENCY_ACTION_CREATOR_DIALOG_UPLOADS",R.direct_playback="LATENCY_ACTION_DIRECT_PLAYBACK",
R.embed="LATENCY_ACTION_EMBED",R.entity_key_serialization_perf="LATENCY_ACTION_ENTITY_KEY_SERIALIZATION_PERF",R.entity_key_deserialization_perf="LATENCY_ACTION_ENTITY_KEY_DESERIALIZATION_PERF",R.explore="LATENCY_ACTION_EXPLORE",R.home="LATENCY_ACTION_HOME",R.library="LATENCY_ACTION_LIBRARY",R.live="LATENCY_ACTION_LIVE",R.live_pagination="LATENCY_ACTION_LIVE_PAGINATION",R.onboarding="LATENCY_ACTION_ONBOARDING",R.parent_profile_settings="LATENCY_ACTION_KIDS_PARENT_PROFILE_SETTINGS",R.parent_tools_collection=
"LATENCY_ACTION_PARENT_TOOLS_COLLECTION",R.parent_tools_dashboard="LATENCY_ACTION_PARENT_TOOLS_DASHBOARD",R.player_att="LATENCY_ACTION_PLAYER_ATTESTATION",R["post.comments"]="LATENCY_ACTION_CREATOR_POST_COMMENTS",R["post.edit"]="LATENCY_ACTION_CREATOR_POST_EDIT",R.prebuffer="LATENCY_ACTION_PREBUFFER",R.prefetch="LATENCY_ACTION_PREFETCH",R.profile_settings="LATENCY_ACTION_KIDS_PROFILE_SETTINGS",R.profile_switcher="LATENCY_ACTION_LOGIN",R.reel_watch="LATENCY_ACTION_REEL_WATCH",R.results="LATENCY_ACTION_RESULTS",
R.search_ui="LATENCY_ACTION_SEARCH_UI",R.search_suggest="LATENCY_ACTION_SUGGEST",R.search_zero_state="LATENCY_ACTION_SEARCH_ZERO_STATE",R.secret_code="LATENCY_ACTION_KIDS_SECRET_CODE",R.seek="LATENCY_ACTION_PLAYER_SEEK",R.settings="LATENCY_ACTION_SETTINGS",R.tenx="LATENCY_ACTION_TENX",R.video_to_ad="LATENCY_ACTION_VIDEO_TO_AD",R.watch="LATENCY_ACTION_WATCH",R.watch_it_again="LATENCY_ACTION_KIDS_WATCH_IT_AGAIN",R["watch,watch7"]="LATENCY_ACTION_WATCH",R["watch,watch7_html5"]="LATENCY_ACTION_WATCH",
R["watch,watch7ad"]="LATENCY_ACTION_WATCH",R["watch,watch7ad_html5"]="LATENCY_ACTION_WATCH",R.wn_comments="LATENCY_ACTION_LOAD_COMMENTS",R.ww_rqs="LATENCY_ACTION_WHO_IS_WATCHING",R["video.analytics"]="LATENCY_ACTION_CREATOR_VIDEO_ANALYTICS",R["video.comments"]="LATENCY_ACTION_CREATOR_VIDEO_COMMENTS",R["video.edit"]="LATENCY_ACTION_CREATOR_VIDEO_EDIT",R["video.editor"]="LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR",R["video.editor_async"]="LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR_ASYNC",R["video.live_settings"]=
"LATENCY_ACTION_CREATOR_VIDEO_LIVE_SETTINGS",R["video.live_streaming"]="LATENCY_ACTION_CREATOR_VIDEO_LIVE_STREAMING",R["video.monetization"]="LATENCY_ACTION_CREATOR_VIDEO_MONETIZATION",R["video.translations"]="LATENCY_ACTION_CREATOR_VIDEO_TRANSLATIONS",R.voice_assistant="LATENCY_ACTION_VOICE_ASSISTANT",R.cast_load_by_entity_to_watch="LATENCY_ACTION_CAST_LOAD_BY_ENTITY_TO_WATCH",R.networkless_performance="LATENCY_ACTION_NETWORKLESS_PERFORMANCE",R),T={},pp=(T.ad_allowed="adTypesAllowed",T.yt_abt="adBreakType",
T.ad_cpn="adClientPlaybackNonce",T.ad_docid="adVideoId",T.yt_ad_an="adNetworks",T.ad_at="adType",T.aida="appInstallDataAgeMs",T.browse_id="browseId",T.p="httpProtocol",T.t="transportProtocol",T.cs="commandSource",T.cpn="clientPlaybackNonce",T.ccs="creatorInfo.creatorCanaryState",T.ctop="creatorInfo.topEntityType",T.csn="clientScreenNonce",T.docid="videoId",T.GetHome_rid="requestIds",T.GetSearch_rid="requestIds",T.GetPlayer_rid="requestIds",T.GetWatchNext_rid="requestIds",T.GetBrowse_rid="requestIds",
T.GetLibrary_rid="requestIds",T.is_continuation="isContinuation",T.is_nav="isNavigation",T.b_p="kabukiInfo.browseParams",T.is_prefetch="kabukiInfo.isPrefetch",T.is_secondary_nav="kabukiInfo.isSecondaryNav",T.nav_type="kabukiInfo.navigationType",T.prev_browse_id="kabukiInfo.prevBrowseId",T.query_source="kabukiInfo.querySource",T.voz_type="kabukiInfo.vozType",T.yt_lt="loadType",T.mver="creatorInfo.measurementVersion",T.yt_ad="isMonetized",T.nr="webInfo.navigationReason",T.nrsu="navigationRequestedSameUrl",
T.ncnp="webInfo.nonPreloadedNodeCount",T.pnt="performanceNavigationTiming",T.prt="playbackRequiresTap",T.plt="playerInfo.playbackType",T.pis="playerInfo.playerInitializedState",T.paused="playerInfo.isPausedOnLoad",T.yt_pt="playerType",T.fmt="playerInfo.itag",T.yt_pl="watchInfo.isPlaylist",T.yt_pre="playerInfo.preloadType",T.yt_ad_pr="prerollAllowed",T.pa="previousAction",T.yt_red="isRedSubscriber",T.rce="mwebInfo.responseContentEncoding",T.rc="resourceInfo.resourceCache",T.scrh="screenHeight",T.scrw=
"screenWidth",T.st="serverTimeMs",T.ssdm="shellStartupDurationMs",T.br_trs="tvInfo.bedrockTriggerState",T.kebqat="kabukiInfo.earlyBrowseRequestInfo.abandonmentType",T.kebqa="kabukiInfo.earlyBrowseRequestInfo.adopted",T.label="tvInfo.label",T.is_mdx="tvInfo.isMdx",T.preloaded="tvInfo.isPreloaded",T.aac_type="tvInfo.authAccessCredentialType",T.upg_player_vis="playerInfo.visibilityState",T.query="unpluggedInfo.query",T.upg_chip_ids_string="unpluggedInfo.upgChipIdsString",T.yt_vst="videoStreamType",T.vph=
"viewportHeight",T.vpw="viewportWidth",T.yt_vis="isVisible",T.rcl="mwebInfo.responseContentLength",T.GetSettings_rid="requestIds",T.GetTrending_rid="requestIds",T.GetMusicSearchSuggestions_rid="requestIds",T.REQUEST_ID="requestIds",T),qp="isContinuation isNavigation kabukiInfo.earlyBrowseRequestInfo.adopted kabukiInfo.isPrefetch kabukiInfo.isSecondaryNav isMonetized navigationRequestedSameUrl performanceNavigationTiming playerInfo.isPausedOnLoad prerollAllowed isRedSubscriber tvInfo.isMdx tvInfo.isPreloaded isVisible watchInfo.isPlaylist playbackRequiresTap".split(" "),
rp={},sp=(rp.ccs="CANARY_STATE_",rp.mver="MEASUREMENT_VERSION_",rp.pis="PLAYER_INITIALIZED_STATE_",rp.yt_pt="LATENCY_PLAYER_",rp.pa="LATENCY_ACTION_",rp.ctop="TOP_ENTITY_TYPE_",rp.yt_vst="VIDEO_STREAM_TYPE_",rp),tp="all_vc ap aq c cbr cbrand cbrver cmodel cos cosver cplatform ctheme cver ei l_an l_mm plid srt yt_fss yt_li vpst vpni2 vpil2 icrc icrt pa GetAccountOverview_rid GetHistory_rid cmt d_vpct d_vpnfi d_vpni nsru pc pfa pfeh pftr pnc prerender psc rc start tcrt tcrc ssr vpr vps yt_abt yt_fn yt_fs yt_pft yt_pre yt_pt yt_pvis ytu_pvis yt_ref yt_sts tds".split(" ");
function up(a){return op[a]||"LATENCY_ACTION_UNKNOWN"}
function vp(a,b,c){c=fp(c);if(c.gelInfos)c.gelInfos[a]=!0;else{var d={};c.gelInfos=(d[a]=!0,d)}if(a.match("_rid")){var e=a.split("_rid")[0];a="REQUEST_ID"}if(a in pp){c=pp[a];0<=bb(qp,c)&&(b=!!b);a in sp&&"string"===typeof b&&(b=sp[a]+b.toUpperCase());a=b;b=c.split(".");for(var f=d={},g=0;g<b.length-1;g++){var h=b[g];f[h]={};f=f[h]}f[b[b.length-1]]="requestIds"===c?[{id:a,endpoint:e}]:a;return Lo({},d)}0<=bb(tp,a)||An(new ik("Unknown label logged with GEL CSI",a))}
;var U={LATENCY_ACTION_KIDS_PROFILE_SWITCHER:90,LATENCY_ACTION_OFFLINE_THUMBNAIL_TRANSFER:100,LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR_ASYNC:46,LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR:37,LATENCY_ACTION_SPINNER_DISPLAYED:14,LATENCY_ACTION_PLAYABILITY_CHECK:10,LATENCY_ACTION_PROCESS:9,LATENCY_ACTION_APP_STARTUP:5,LATENCY_ACTION_PLAYER_ROTATION:150,LATENCY_ACTION_SHOPPING_IN_APP:124,LATENCY_ACTION_PLAYER_ATTESTATION:121,LATENCY_ACTION_PLAYER_SEEK:119,LATENCY_ACTION_SUPER_STICKER_BUY_FLOW:114,LATENCY_ACTION_BLOCKS_PERFORMANCE:148,
LATENCY_ACTION_ASSISTANT_QUERY:138,LATENCY_ACTION_ASSISTANT_SETTINGS:137,LATENCY_ACTION_ENTITY_KEY_DESERIALIZATION_PERF:129,LATENCY_ACTION_ENTITY_KEY_SERIALIZATION_PERF:128,LATENCY_ACTION_PROOF_OF_ORIGIN_TOKEN_CREATE:127,LATENCY_ACTION_EMBEDS_SDK_INITIALIZATION:123,LATENCY_ACTION_NETWORKLESS_PERFORMANCE:122,LATENCY_ACTION_DOWNLOADS_EXPANSION:133,LATENCY_ACTION_ENTITY_TRANSFORM:131,LATENCY_ACTION_DOWNLOADS_COMPATIBILITY_LAYER:96,LATENCY_ACTION_EMBEDS_SET_VIDEO:95,LATENCY_ACTION_SETTINGS:93,LATENCY_ACTION_ABANDONED_STARTUP:81,
LATENCY_ACTION_MEDIA_BROWSER_ALARM_PLAY:80,LATENCY_ACTION_MEDIA_BROWSER_SEARCH:79,LATENCY_ACTION_MEDIA_BROWSER_LOAD_TREE:78,LATENCY_ACTION_WHO_IS_WATCHING:77,LATENCY_ACTION_CAST_LOAD_BY_ENTITY_TO_WATCH:76,LATENCY_ACTION_LITE_SWITCH_ACCOUNT:73,LATENCY_ACTION_ELEMENTS_PERFORMANCE:70,LATENCY_ACTION_LOCATION_SIGNAL_COLLECTION:69,LATENCY_ACTION_MODIFY_CHANNEL_NOTIFICATION:65,LATENCY_ACTION_OFFLINE_STORE_START:61,LATENCY_ACTION_REEL_EDITOR:58,LATENCY_ACTION_CHANNEL_SUBSCRIBE:56,LATENCY_ACTION_CHANNEL_PREVIEW:55,
LATENCY_ACTION_PREFETCH:52,LATENCY_ACTION_ABANDONED_WATCH:45,LATENCY_ACTION_LOAD_COMMENT_REPLIES:26,LATENCY_ACTION_LOAD_COMMENTS:25,LATENCY_ACTION_EDIT_COMMENT:24,LATENCY_ACTION_NEW_COMMENT:23,LATENCY_ACTION_OFFLINE_SHARING_RECEIVER_PAIRING:19,LATENCY_ACTION_EMBED:18,LATENCY_ACTION_MDX_LAUNCH:15,LATENCY_ACTION_RESOLVE_URL:13,LATENCY_ACTION_CAST_SPLASH:149,LATENCY_ACTION_MDX_CAST:120,LATENCY_ACTION_MDX_COMMAND:12,LATENCY_ACTION_REEL_SELECT_SEGMENT:136,LATENCY_ACTION_ACCELERATED_EFFECTS:145,LATENCY_ACTION_UPLOAD_AUDIO_MIXER:147,
LATENCY_ACTION_SHORTS_SEG_IMP_TRANSCODING:146,LATENCY_ACTION_SHORTS_AUDIO_PICKER_PLAYBACK:130,LATENCY_ACTION_SHORTS_WAVEFORM_DOWNLOAD:125,LATENCY_ACTION_SHORTS_GALLERY:107,LATENCY_ACTION_SHORTS_TRIM:105,LATENCY_ACTION_SHORTS_EDIT:104,LATENCY_ACTION_SHORTS_CAMERA:103,LATENCY_ACTION_PARENT_TOOLS_DASHBOARD:102,LATENCY_ACTION_PARENT_TOOLS_COLLECTION:101,LATENCY_ACTION_MUSIC_LOAD_RECOMMENDED_MEDIA_ITEMS:116,LATENCY_ACTION_MUSIC_LOAD_MEDIA_ITEMS:115,LATENCY_ACTION_MUSIC_ALBUM_DETAIL:72,LATENCY_ACTION_MUSIC_PLAYLIST_DETAIL:71,
LATENCY_ACTION_CHIPS:68,LATENCY_ACTION_SEARCH_ZERO_STATE:67,LATENCY_ACTION_LIVE_PAGINATION:117,LATENCY_ACTION_LIVE:20,LATENCY_ACTION_PREBUFFER:40,LATENCY_ACTION_TENX:39,LATENCY_ACTION_KIDS_PROFILE_SETTINGS:94,LATENCY_ACTION_KIDS_WATCH_IT_AGAIN:92,LATENCY_ACTION_KIDS_SECRET_CODE:91,LATENCY_ACTION_KIDS_PARENT_PROFILE_SETTINGS:89,LATENCY_ACTION_KIDS_ONBOARDING:88,LATENCY_ACTION_KIDS_VOICE_SEARCH:82,LATENCY_ACTION_KIDS_CURATED_COLLECTION:62,LATENCY_ACTION_KIDS_LIBRARY:53,LATENCY_ACTION_CREATOR_VIDEO_TRANSLATIONS:38,
LATENCY_ACTION_CREATOR_VIDEO_MONETIZATION:74,LATENCY_ACTION_CREATOR_VIDEO_LIVE_STREAMING:141,LATENCY_ACTION_CREATOR_VIDEO_LIVE_SETTINGS:142,LATENCY_ACTION_CREATOR_VIDEO_EDITOR_ASYNC:51,LATENCY_ACTION_CREATOR_VIDEO_EDITOR:50,LATENCY_ACTION_CREATOR_VIDEO_EDIT:36,LATENCY_ACTION_CREATOR_VIDEO_COMMENTS:34,LATENCY_ACTION_CREATOR_VIDEO_ANALYTICS:33,LATENCY_ACTION_CREATOR_POST_LIST:112,LATENCY_ACTION_CREATOR_POST_EDIT:110,LATENCY_ACTION_CREATOR_POST_COMMENTS:111,LATENCY_ACTION_CREATOR_LIVE_STREAMING:108,
LATENCY_ACTION_CREATOR_DIALOG_UPLOADS:86,LATENCY_ACTION_CREATOR_DIALOG_COPYRIGHT_STRIKES:87,LATENCY_ACTION_CREATOR_CHANNEL_VIDEOS:32,LATENCY_ACTION_CREATOR_CHANNEL_TRANSLATIONS:48,LATENCY_ACTION_CREATOR_CHANNEL_PLAYLISTS:139,LATENCY_ACTION_CREATOR_CHANNEL_MUSIC:99,LATENCY_ACTION_CREATOR_CHANNEL_MONETIZATION:43,LATENCY_ACTION_CREATOR_CHANNEL_EDITING:113,LATENCY_ACTION_CREATOR_CHANNEL_DASHBOARD:49,LATENCY_ACTION_CREATOR_CHANNEL_COPYRIGHT:44,LATENCY_ACTION_CREATOR_CHANNEL_COMMENTS:66,LATENCY_ACTION_CREATOR_CHANNEL_ANALYTICS:31,
LATENCY_ACTION_CREATOR_ARTIST_PROFILE:85,LATENCY_ACTION_CREATOR_ARTIST_CONCERTS:84,LATENCY_ACTION_CREATOR_ARTIST_ANALYTICS:83,LATENCY_ACTION_CREATOR_ANALYTICS_EXPLORE:140,LATENCY_ACTION_STORYBOARD_THUMBNAILS:118,LATENCY_ACTION_SEARCH_THUMBNAILS:59,LATENCY_ACTION_ON_DEVICE_MODEL_DOWNLOAD:54,LATENCY_ACTION_VOICE_ASSISTANT:47,LATENCY_ACTION_SEARCH_UI:35,LATENCY_ACTION_SUGGEST:30,LATENCY_ACTION_AUTO_SEARCH:126,LATENCY_ACTION_DOWNLOADS:98,LATENCY_ACTION_EXPLORE:75,LATENCY_ACTION_VIDEO_LIST:63,LATENCY_ACTION_HOME_RESUME:60,
LATENCY_ACTION_SUBSCRIPTIONS_LIST:57,LATENCY_ACTION_THUMBNAIL_LOAD:42,LATENCY_ACTION_FIRST_THUMBNAIL_LOAD:29,LATENCY_ACTION_SUBSCRIPTIONS_FEED:109,LATENCY_ACTION_SUBSCRIPTIONS:28,LATENCY_ACTION_TRENDING:27,LATENCY_ACTION_LIBRARY:21,LATENCY_ACTION_VIDEO_THUMBNAIL:8,LATENCY_ACTION_SHOW_MORE:7,LATENCY_ACTION_VIDEO_PREVIEW:6,LATENCY_ACTION_PREBUFFER_VIDEO:144,LATENCY_ACTION_PREFETCH_VIDEO:143,LATENCY_ACTION_DIRECT_PLAYBACK:132,LATENCY_ACTION_REEL_WATCH:41,LATENCY_ACTION_AD_TO_AD:22,LATENCY_ACTION_VIDEO_TO_AD:17,
LATENCY_ACTION_AD_TO_VIDEO:16,LATENCY_ACTION_ONBOARDING:135,LATENCY_ACTION_LOGIN:97,LATENCY_ACTION_BROWSE:11,LATENCY_ACTION_CHANNELS:4,LATENCY_ACTION_WATCH:3,LATENCY_ACTION_RESULTS:2,LATENCY_ACTION_HOME:1,LATENCY_ACTION_STARTUP:106,LATENCY_ACTION_UNKNOWN:0};U[U.LATENCY_ACTION_KIDS_PROFILE_SWITCHER]="LATENCY_ACTION_KIDS_PROFILE_SWITCHER";U[U.LATENCY_ACTION_OFFLINE_THUMBNAIL_TRANSFER]="LATENCY_ACTION_OFFLINE_THUMBNAIL_TRANSFER";U[U.LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR_ASYNC]="LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR_ASYNC";
U[U.LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR]="LATENCY_ACTION_CREATOR_VIDEO_VIDEO_EDITOR";U[U.LATENCY_ACTION_SPINNER_DISPLAYED]="LATENCY_ACTION_SPINNER_DISPLAYED";U[U.LATENCY_ACTION_PLAYABILITY_CHECK]="LATENCY_ACTION_PLAYABILITY_CHECK";U[U.LATENCY_ACTION_PROCESS]="LATENCY_ACTION_PROCESS";U[U.LATENCY_ACTION_APP_STARTUP]="LATENCY_ACTION_APP_STARTUP";U[U.LATENCY_ACTION_PLAYER_ROTATION]="LATENCY_ACTION_PLAYER_ROTATION";U[U.LATENCY_ACTION_SHOPPING_IN_APP]="LATENCY_ACTION_SHOPPING_IN_APP";
U[U.LATENCY_ACTION_PLAYER_ATTESTATION]="LATENCY_ACTION_PLAYER_ATTESTATION";U[U.LATENCY_ACTION_PLAYER_SEEK]="LATENCY_ACTION_PLAYER_SEEK";U[U.LATENCY_ACTION_SUPER_STICKER_BUY_FLOW]="LATENCY_ACTION_SUPER_STICKER_BUY_FLOW";U[U.LATENCY_ACTION_BLOCKS_PERFORMANCE]="LATENCY_ACTION_BLOCKS_PERFORMANCE";U[U.LATENCY_ACTION_ASSISTANT_QUERY]="LATENCY_ACTION_ASSISTANT_QUERY";U[U.LATENCY_ACTION_ASSISTANT_SETTINGS]="LATENCY_ACTION_ASSISTANT_SETTINGS";U[U.LATENCY_ACTION_ENTITY_KEY_DESERIALIZATION_PERF]="LATENCY_ACTION_ENTITY_KEY_DESERIALIZATION_PERF";
U[U.LATENCY_ACTION_ENTITY_KEY_SERIALIZATION_PERF]="LATENCY_ACTION_ENTITY_KEY_SERIALIZATION_PERF";U[U.LATENCY_ACTION_PROOF_OF_ORIGIN_TOKEN_CREATE]="LATENCY_ACTION_PROOF_OF_ORIGIN_TOKEN_CREATE";U[U.LATENCY_ACTION_EMBEDS_SDK_INITIALIZATION]="LATENCY_ACTION_EMBEDS_SDK_INITIALIZATION";U[U.LATENCY_ACTION_NETWORKLESS_PERFORMANCE]="LATENCY_ACTION_NETWORKLESS_PERFORMANCE";U[U.LATENCY_ACTION_DOWNLOADS_EXPANSION]="LATENCY_ACTION_DOWNLOADS_EXPANSION";U[U.LATENCY_ACTION_ENTITY_TRANSFORM]="LATENCY_ACTION_ENTITY_TRANSFORM";
U[U.LATENCY_ACTION_DOWNLOADS_COMPATIBILITY_LAYER]="LATENCY_ACTION_DOWNLOADS_COMPATIBILITY_LAYER";U[U.LATENCY_ACTION_EMBEDS_SET_VIDEO]="LATENCY_ACTION_EMBEDS_SET_VIDEO";U[U.LATENCY_ACTION_SETTINGS]="LATENCY_ACTION_SETTINGS";U[U.LATENCY_ACTION_ABANDONED_STARTUP]="LATENCY_ACTION_ABANDONED_STARTUP";U[U.LATENCY_ACTION_MEDIA_BROWSER_ALARM_PLAY]="LATENCY_ACTION_MEDIA_BROWSER_ALARM_PLAY";U[U.LATENCY_ACTION_MEDIA_BROWSER_SEARCH]="LATENCY_ACTION_MEDIA_BROWSER_SEARCH";
U[U.LATENCY_ACTION_MEDIA_BROWSER_LOAD_TREE]="LATENCY_ACTION_MEDIA_BROWSER_LOAD_TREE";U[U.LATENCY_ACTION_WHO_IS_WATCHING]="LATENCY_ACTION_WHO_IS_WATCHING";U[U.LATENCY_ACTION_CAST_LOAD_BY_ENTITY_TO_WATCH]="LATENCY_ACTION_CAST_LOAD_BY_ENTITY_TO_WATCH";U[U.LATENCY_ACTION_LITE_SWITCH_ACCOUNT]="LATENCY_ACTION_LITE_SWITCH_ACCOUNT";U[U.LATENCY_ACTION_ELEMENTS_PERFORMANCE]="LATENCY_ACTION_ELEMENTS_PERFORMANCE";U[U.LATENCY_ACTION_LOCATION_SIGNAL_COLLECTION]="LATENCY_ACTION_LOCATION_SIGNAL_COLLECTION";
U[U.LATENCY_ACTION_MODIFY_CHANNEL_NOTIFICATION]="LATENCY_ACTION_MODIFY_CHANNEL_NOTIFICATION";U[U.LATENCY_ACTION_OFFLINE_STORE_START]="LATENCY_ACTION_OFFLINE_STORE_START";U[U.LATENCY_ACTION_REEL_EDITOR]="LATENCY_ACTION_REEL_EDITOR";U[U.LATENCY_ACTION_CHANNEL_SUBSCRIBE]="LATENCY_ACTION_CHANNEL_SUBSCRIBE";U[U.LATENCY_ACTION_CHANNEL_PREVIEW]="LATENCY_ACTION_CHANNEL_PREVIEW";U[U.LATENCY_ACTION_PREFETCH]="LATENCY_ACTION_PREFETCH";U[U.LATENCY_ACTION_ABANDONED_WATCH]="LATENCY_ACTION_ABANDONED_WATCH";
U[U.LATENCY_ACTION_LOAD_COMMENT_REPLIES]="LATENCY_ACTION_LOAD_COMMENT_REPLIES";U[U.LATENCY_ACTION_LOAD_COMMENTS]="LATENCY_ACTION_LOAD_COMMENTS";U[U.LATENCY_ACTION_EDIT_COMMENT]="LATENCY_ACTION_EDIT_COMMENT";U[U.LATENCY_ACTION_NEW_COMMENT]="LATENCY_ACTION_NEW_COMMENT";U[U.LATENCY_ACTION_OFFLINE_SHARING_RECEIVER_PAIRING]="LATENCY_ACTION_OFFLINE_SHARING_RECEIVER_PAIRING";U[U.LATENCY_ACTION_EMBED]="LATENCY_ACTION_EMBED";U[U.LATENCY_ACTION_MDX_LAUNCH]="LATENCY_ACTION_MDX_LAUNCH";
U[U.LATENCY_ACTION_RESOLVE_URL]="LATENCY_ACTION_RESOLVE_URL";U[U.LATENCY_ACTION_CAST_SPLASH]="LATENCY_ACTION_CAST_SPLASH";U[U.LATENCY_ACTION_MDX_CAST]="LATENCY_ACTION_MDX_CAST";U[U.LATENCY_ACTION_MDX_COMMAND]="LATENCY_ACTION_MDX_COMMAND";U[U.LATENCY_ACTION_REEL_SELECT_SEGMENT]="LATENCY_ACTION_REEL_SELECT_SEGMENT";U[U.LATENCY_ACTION_ACCELERATED_EFFECTS]="LATENCY_ACTION_ACCELERATED_EFFECTS";U[U.LATENCY_ACTION_UPLOAD_AUDIO_MIXER]="LATENCY_ACTION_UPLOAD_AUDIO_MIXER";
U[U.LATENCY_ACTION_SHORTS_SEG_IMP_TRANSCODING]="LATENCY_ACTION_SHORTS_SEG_IMP_TRANSCODING";U[U.LATENCY_ACTION_SHORTS_AUDIO_PICKER_PLAYBACK]="LATENCY_ACTION_SHORTS_AUDIO_PICKER_PLAYBACK";U[U.LATENCY_ACTION_SHORTS_WAVEFORM_DOWNLOAD]="LATENCY_ACTION_SHORTS_WAVEFORM_DOWNLOAD";U[U.LATENCY_ACTION_SHORTS_GALLERY]="LATENCY_ACTION_SHORTS_GALLERY";U[U.LATENCY_ACTION_SHORTS_TRIM]="LATENCY_ACTION_SHORTS_TRIM";U[U.LATENCY_ACTION_SHORTS_EDIT]="LATENCY_ACTION_SHORTS_EDIT";U[U.LATENCY_ACTION_SHORTS_CAMERA]="LATENCY_ACTION_SHORTS_CAMERA";
U[U.LATENCY_ACTION_PARENT_TOOLS_DASHBOARD]="LATENCY_ACTION_PARENT_TOOLS_DASHBOARD";U[U.LATENCY_ACTION_PARENT_TOOLS_COLLECTION]="LATENCY_ACTION_PARENT_TOOLS_COLLECTION";U[U.LATENCY_ACTION_MUSIC_LOAD_RECOMMENDED_MEDIA_ITEMS]="LATENCY_ACTION_MUSIC_LOAD_RECOMMENDED_MEDIA_ITEMS";U[U.LATENCY_ACTION_MUSIC_LOAD_MEDIA_ITEMS]="LATENCY_ACTION_MUSIC_LOAD_MEDIA_ITEMS";U[U.LATENCY_ACTION_MUSIC_ALBUM_DETAIL]="LATENCY_ACTION_MUSIC_ALBUM_DETAIL";U[U.LATENCY_ACTION_MUSIC_PLAYLIST_DETAIL]="LATENCY_ACTION_MUSIC_PLAYLIST_DETAIL";
U[U.LATENCY_ACTION_CHIPS]="LATENCY_ACTION_CHIPS";U[U.LATENCY_ACTION_SEARCH_ZERO_STATE]="LATENCY_ACTION_SEARCH_ZERO_STATE";U[U.LATENCY_ACTION_LIVE_PAGINATION]="LATENCY_ACTION_LIVE_PAGINATION";U[U.LATENCY_ACTION_LIVE]="LATENCY_ACTION_LIVE";U[U.LATENCY_ACTION_PREBUFFER]="LATENCY_ACTION_PREBUFFER";U[U.LATENCY_ACTION_TENX]="LATENCY_ACTION_TENX";U[U.LATENCY_ACTION_KIDS_PROFILE_SETTINGS]="LATENCY_ACTION_KIDS_PROFILE_SETTINGS";U[U.LATENCY_ACTION_KIDS_WATCH_IT_AGAIN]="LATENCY_ACTION_KIDS_WATCH_IT_AGAIN";
U[U.LATENCY_ACTION_KIDS_SECRET_CODE]="LATENCY_ACTION_KIDS_SECRET_CODE";U[U.LATENCY_ACTION_KIDS_PARENT_PROFILE_SETTINGS]="LATENCY_ACTION_KIDS_PARENT_PROFILE_SETTINGS";U[U.LATENCY_ACTION_KIDS_ONBOARDING]="LATENCY_ACTION_KIDS_ONBOARDING";U[U.LATENCY_ACTION_KIDS_VOICE_SEARCH]="LATENCY_ACTION_KIDS_VOICE_SEARCH";U[U.LATENCY_ACTION_KIDS_CURATED_COLLECTION]="LATENCY_ACTION_KIDS_CURATED_COLLECTION";U[U.LATENCY_ACTION_KIDS_LIBRARY]="LATENCY_ACTION_KIDS_LIBRARY";
U[U.LATENCY_ACTION_CREATOR_VIDEO_TRANSLATIONS]="LATENCY_ACTION_CREATOR_VIDEO_TRANSLATIONS";U[U.LATENCY_ACTION_CREATOR_VIDEO_MONETIZATION]="LATENCY_ACTION_CREATOR_VIDEO_MONETIZATION";U[U.LATENCY_ACTION_CREATOR_VIDEO_LIVE_STREAMING]="LATENCY_ACTION_CREATOR_VIDEO_LIVE_STREAMING";U[U.LATENCY_ACTION_CREATOR_VIDEO_LIVE_SETTINGS]="LATENCY_ACTION_CREATOR_VIDEO_LIVE_SETTINGS";U[U.LATENCY_ACTION_CREATOR_VIDEO_EDITOR_ASYNC]="LATENCY_ACTION_CREATOR_VIDEO_EDITOR_ASYNC";
U[U.LATENCY_ACTION_CREATOR_VIDEO_EDITOR]="LATENCY_ACTION_CREATOR_VIDEO_EDITOR";U[U.LATENCY_ACTION_CREATOR_VIDEO_EDIT]="LATENCY_ACTION_CREATOR_VIDEO_EDIT";U[U.LATENCY_ACTION_CREATOR_VIDEO_COMMENTS]="LATENCY_ACTION_CREATOR_VIDEO_COMMENTS";U[U.LATENCY_ACTION_CREATOR_VIDEO_ANALYTICS]="LATENCY_ACTION_CREATOR_VIDEO_ANALYTICS";U[U.LATENCY_ACTION_CREATOR_POST_LIST]="LATENCY_ACTION_CREATOR_POST_LIST";U[U.LATENCY_ACTION_CREATOR_POST_EDIT]="LATENCY_ACTION_CREATOR_POST_EDIT";
U[U.LATENCY_ACTION_CREATOR_POST_COMMENTS]="LATENCY_ACTION_CREATOR_POST_COMMENTS";U[U.LATENCY_ACTION_CREATOR_LIVE_STREAMING]="LATENCY_ACTION_CREATOR_LIVE_STREAMING";U[U.LATENCY_ACTION_CREATOR_DIALOG_UPLOADS]="LATENCY_ACTION_CREATOR_DIALOG_UPLOADS";U[U.LATENCY_ACTION_CREATOR_DIALOG_COPYRIGHT_STRIKES]="LATENCY_ACTION_CREATOR_DIALOG_COPYRIGHT_STRIKES";U[U.LATENCY_ACTION_CREATOR_CHANNEL_VIDEOS]="LATENCY_ACTION_CREATOR_CHANNEL_VIDEOS";U[U.LATENCY_ACTION_CREATOR_CHANNEL_TRANSLATIONS]="LATENCY_ACTION_CREATOR_CHANNEL_TRANSLATIONS";
U[U.LATENCY_ACTION_CREATOR_CHANNEL_PLAYLISTS]="LATENCY_ACTION_CREATOR_CHANNEL_PLAYLISTS";U[U.LATENCY_ACTION_CREATOR_CHANNEL_MUSIC]="LATENCY_ACTION_CREATOR_CHANNEL_MUSIC";U[U.LATENCY_ACTION_CREATOR_CHANNEL_MONETIZATION]="LATENCY_ACTION_CREATOR_CHANNEL_MONETIZATION";U[U.LATENCY_ACTION_CREATOR_CHANNEL_EDITING]="LATENCY_ACTION_CREATOR_CHANNEL_EDITING";U[U.LATENCY_ACTION_CREATOR_CHANNEL_DASHBOARD]="LATENCY_ACTION_CREATOR_CHANNEL_DASHBOARD";U[U.LATENCY_ACTION_CREATOR_CHANNEL_COPYRIGHT]="LATENCY_ACTION_CREATOR_CHANNEL_COPYRIGHT";
U[U.LATENCY_ACTION_CREATOR_CHANNEL_COMMENTS]="LATENCY_ACTION_CREATOR_CHANNEL_COMMENTS";U[U.LATENCY_ACTION_CREATOR_CHANNEL_ANALYTICS]="LATENCY_ACTION_CREATOR_CHANNEL_ANALYTICS";U[U.LATENCY_ACTION_CREATOR_ARTIST_PROFILE]="LATENCY_ACTION_CREATOR_ARTIST_PROFILE";U[U.LATENCY_ACTION_CREATOR_ARTIST_CONCERTS]="LATENCY_ACTION_CREATOR_ARTIST_CONCERTS";U[U.LATENCY_ACTION_CREATOR_ARTIST_ANALYTICS]="LATENCY_ACTION_CREATOR_ARTIST_ANALYTICS";U[U.LATENCY_ACTION_CREATOR_ANALYTICS_EXPLORE]="LATENCY_ACTION_CREATOR_ANALYTICS_EXPLORE";
U[U.LATENCY_ACTION_STORYBOARD_THUMBNAILS]="LATENCY_ACTION_STORYBOARD_THUMBNAILS";U[U.LATENCY_ACTION_SEARCH_THUMBNAILS]="LATENCY_ACTION_SEARCH_THUMBNAILS";U[U.LATENCY_ACTION_ON_DEVICE_MODEL_DOWNLOAD]="LATENCY_ACTION_ON_DEVICE_MODEL_DOWNLOAD";U[U.LATENCY_ACTION_VOICE_ASSISTANT]="LATENCY_ACTION_VOICE_ASSISTANT";U[U.LATENCY_ACTION_SEARCH_UI]="LATENCY_ACTION_SEARCH_UI";U[U.LATENCY_ACTION_SUGGEST]="LATENCY_ACTION_SUGGEST";U[U.LATENCY_ACTION_AUTO_SEARCH]="LATENCY_ACTION_AUTO_SEARCH";
U[U.LATENCY_ACTION_DOWNLOADS]="LATENCY_ACTION_DOWNLOADS";U[U.LATENCY_ACTION_EXPLORE]="LATENCY_ACTION_EXPLORE";U[U.LATENCY_ACTION_VIDEO_LIST]="LATENCY_ACTION_VIDEO_LIST";U[U.LATENCY_ACTION_HOME_RESUME]="LATENCY_ACTION_HOME_RESUME";U[U.LATENCY_ACTION_SUBSCRIPTIONS_LIST]="LATENCY_ACTION_SUBSCRIPTIONS_LIST";U[U.LATENCY_ACTION_THUMBNAIL_LOAD]="LATENCY_ACTION_THUMBNAIL_LOAD";U[U.LATENCY_ACTION_FIRST_THUMBNAIL_LOAD]="LATENCY_ACTION_FIRST_THUMBNAIL_LOAD";U[U.LATENCY_ACTION_SUBSCRIPTIONS_FEED]="LATENCY_ACTION_SUBSCRIPTIONS_FEED";
U[U.LATENCY_ACTION_SUBSCRIPTIONS]="LATENCY_ACTION_SUBSCRIPTIONS";U[U.LATENCY_ACTION_TRENDING]="LATENCY_ACTION_TRENDING";U[U.LATENCY_ACTION_LIBRARY]="LATENCY_ACTION_LIBRARY";U[U.LATENCY_ACTION_VIDEO_THUMBNAIL]="LATENCY_ACTION_VIDEO_THUMBNAIL";U[U.LATENCY_ACTION_SHOW_MORE]="LATENCY_ACTION_SHOW_MORE";U[U.LATENCY_ACTION_VIDEO_PREVIEW]="LATENCY_ACTION_VIDEO_PREVIEW";U[U.LATENCY_ACTION_PREBUFFER_VIDEO]="LATENCY_ACTION_PREBUFFER_VIDEO";U[U.LATENCY_ACTION_PREFETCH_VIDEO]="LATENCY_ACTION_PREFETCH_VIDEO";
U[U.LATENCY_ACTION_DIRECT_PLAYBACK]="LATENCY_ACTION_DIRECT_PLAYBACK";U[U.LATENCY_ACTION_REEL_WATCH]="LATENCY_ACTION_REEL_WATCH";U[U.LATENCY_ACTION_AD_TO_AD]="LATENCY_ACTION_AD_TO_AD";U[U.LATENCY_ACTION_VIDEO_TO_AD]="LATENCY_ACTION_VIDEO_TO_AD";U[U.LATENCY_ACTION_AD_TO_VIDEO]="LATENCY_ACTION_AD_TO_VIDEO";U[U.LATENCY_ACTION_ONBOARDING]="LATENCY_ACTION_ONBOARDING";U[U.LATENCY_ACTION_LOGIN]="LATENCY_ACTION_LOGIN";U[U.LATENCY_ACTION_BROWSE]="LATENCY_ACTION_BROWSE";U[U.LATENCY_ACTION_CHANNELS]="LATENCY_ACTION_CHANNELS";
U[U.LATENCY_ACTION_WATCH]="LATENCY_ACTION_WATCH";U[U.LATENCY_ACTION_RESULTS]="LATENCY_ACTION_RESULTS";U[U.LATENCY_ACTION_HOME]="LATENCY_ACTION_HOME";U[U.LATENCY_ACTION_STARTUP]="LATENCY_ACTION_STARTUP";U[U.LATENCY_ACTION_UNKNOWN]="LATENCY_ACTION_UNKNOWN";var wp={LATENCY_NETWORK_MOBILE:2,LATENCY_NETWORK_WIFI:1,LATENCY_NETWORK_UNKNOWN:0};wp[wp.LATENCY_NETWORK_MOBILE]="LATENCY_NETWORK_MOBILE";wp[wp.LATENCY_NETWORK_WIFI]="LATENCY_NETWORK_WIFI";wp[wp.LATENCY_NETWORK_UNKNOWN]="LATENCY_NETWORK_UNKNOWN";
var V={CONN_INVALID:31,CONN_CELLULAR_5G_NSA:12,CONN_CELLULAR_5G_SA:11,CONN_WIFI_METERED:10,CONN_CELLULAR_5G:9,CONN_DISCO:8,CONN_CELLULAR_UNKNOWN:7,CONN_CELLULAR_4G:6,CONN_CELLULAR_3G:5,CONN_CELLULAR_2G:4,CONN_WIFI:3,CONN_NONE:2,CONN_UNKNOWN:1,CONN_DEFAULT:0};V[V.CONN_INVALID]="CONN_INVALID";V[V.CONN_CELLULAR_5G_NSA]="CONN_CELLULAR_5G_NSA";V[V.CONN_CELLULAR_5G_SA]="CONN_CELLULAR_5G_SA";V[V.CONN_WIFI_METERED]="CONN_WIFI_METERED";V[V.CONN_CELLULAR_5G]="CONN_CELLULAR_5G";V[V.CONN_DISCO]="CONN_DISCO";
V[V.CONN_CELLULAR_UNKNOWN]="CONN_CELLULAR_UNKNOWN";V[V.CONN_CELLULAR_4G]="CONN_CELLULAR_4G";V[V.CONN_CELLULAR_3G]="CONN_CELLULAR_3G";V[V.CONN_CELLULAR_2G]="CONN_CELLULAR_2G";V[V.CONN_WIFI]="CONN_WIFI";V[V.CONN_NONE]="CONN_NONE";V[V.CONN_UNKNOWN]="CONN_UNKNOWN";V[V.CONN_DEFAULT]="CONN_DEFAULT";
var X={DETAILED_NETWORK_TYPE_NR_NSA:126,DETAILED_NETWORK_TYPE_NR_SA:125,DETAILED_NETWORK_TYPE_INTERNAL_WIFI_IMPAIRED:124,DETAILED_NETWORK_TYPE_APP_WIFI_HOTSPOT:123,DETAILED_NETWORK_TYPE_DISCONNECTED:122,DETAILED_NETWORK_TYPE_NON_MOBILE_UNKNOWN:121,DETAILED_NETWORK_TYPE_MOBILE_UNKNOWN:120,DETAILED_NETWORK_TYPE_WIMAX:119,DETAILED_NETWORK_TYPE_ETHERNET:118,DETAILED_NETWORK_TYPE_BLUETOOTH:117,DETAILED_NETWORK_TYPE_WIFI:116,DETAILED_NETWORK_TYPE_LTE:115,DETAILED_NETWORK_TYPE_HSPAP:114,DETAILED_NETWORK_TYPE_EHRPD:113,
DETAILED_NETWORK_TYPE_EVDO_B:112,DETAILED_NETWORK_TYPE_UMTS:111,DETAILED_NETWORK_TYPE_IDEN:110,DETAILED_NETWORK_TYPE_HSUPA:109,DETAILED_NETWORK_TYPE_HSPA:108,DETAILED_NETWORK_TYPE_HSDPA:107,DETAILED_NETWORK_TYPE_EVDO_A:106,DETAILED_NETWORK_TYPE_EVDO_0:105,DETAILED_NETWORK_TYPE_CDMA:104,DETAILED_NETWORK_TYPE_1_X_RTT:103,DETAILED_NETWORK_TYPE_GPRS:102,DETAILED_NETWORK_TYPE_EDGE:101,DETAILED_NETWORK_TYPE_UNKNOWN:0};X[X.DETAILED_NETWORK_TYPE_NR_NSA]="DETAILED_NETWORK_TYPE_NR_NSA";
X[X.DETAILED_NETWORK_TYPE_NR_SA]="DETAILED_NETWORK_TYPE_NR_SA";X[X.DETAILED_NETWORK_TYPE_INTERNAL_WIFI_IMPAIRED]="DETAILED_NETWORK_TYPE_INTERNAL_WIFI_IMPAIRED";X[X.DETAILED_NETWORK_TYPE_APP_WIFI_HOTSPOT]="DETAILED_NETWORK_TYPE_APP_WIFI_HOTSPOT";X[X.DETAILED_NETWORK_TYPE_DISCONNECTED]="DETAILED_NETWORK_TYPE_DISCONNECTED";X[X.DETAILED_NETWORK_TYPE_NON_MOBILE_UNKNOWN]="DETAILED_NETWORK_TYPE_NON_MOBILE_UNKNOWN";X[X.DETAILED_NETWORK_TYPE_MOBILE_UNKNOWN]="DETAILED_NETWORK_TYPE_MOBILE_UNKNOWN";
X[X.DETAILED_NETWORK_TYPE_WIMAX]="DETAILED_NETWORK_TYPE_WIMAX";X[X.DETAILED_NETWORK_TYPE_ETHERNET]="DETAILED_NETWORK_TYPE_ETHERNET";X[X.DETAILED_NETWORK_TYPE_BLUETOOTH]="DETAILED_NETWORK_TYPE_BLUETOOTH";X[X.DETAILED_NETWORK_TYPE_WIFI]="DETAILED_NETWORK_TYPE_WIFI";X[X.DETAILED_NETWORK_TYPE_LTE]="DETAILED_NETWORK_TYPE_LTE";X[X.DETAILED_NETWORK_TYPE_HSPAP]="DETAILED_NETWORK_TYPE_HSPAP";X[X.DETAILED_NETWORK_TYPE_EHRPD]="DETAILED_NETWORK_TYPE_EHRPD";X[X.DETAILED_NETWORK_TYPE_EVDO_B]="DETAILED_NETWORK_TYPE_EVDO_B";
X[X.DETAILED_NETWORK_TYPE_UMTS]="DETAILED_NETWORK_TYPE_UMTS";X[X.DETAILED_NETWORK_TYPE_IDEN]="DETAILED_NETWORK_TYPE_IDEN";X[X.DETAILED_NETWORK_TYPE_HSUPA]="DETAILED_NETWORK_TYPE_HSUPA";X[X.DETAILED_NETWORK_TYPE_HSPA]="DETAILED_NETWORK_TYPE_HSPA";X[X.DETAILED_NETWORK_TYPE_HSDPA]="DETAILED_NETWORK_TYPE_HSDPA";X[X.DETAILED_NETWORK_TYPE_EVDO_A]="DETAILED_NETWORK_TYPE_EVDO_A";X[X.DETAILED_NETWORK_TYPE_EVDO_0]="DETAILED_NETWORK_TYPE_EVDO_0";X[X.DETAILED_NETWORK_TYPE_CDMA]="DETAILED_NETWORK_TYPE_CDMA";
X[X.DETAILED_NETWORK_TYPE_1_X_RTT]="DETAILED_NETWORK_TYPE_1_X_RTT";X[X.DETAILED_NETWORK_TYPE_GPRS]="DETAILED_NETWORK_TYPE_GPRS";X[X.DETAILED_NETWORK_TYPE_EDGE]="DETAILED_NETWORK_TYPE_EDGE";X[X.DETAILED_NETWORK_TYPE_UNKNOWN]="DETAILED_NETWORK_TYPE_UNKNOWN";var xp={LATENCY_PLAYER_RTSP:7,LATENCY_PLAYER_HTML5_INLINE:6,LATENCY_PLAYER_HTML5_FULLSCREEN:5,LATENCY_PLAYER_HTML5:4,LATENCY_PLAYER_FRAMEWORK:3,LATENCY_PLAYER_FLASH:2,LATENCY_PLAYER_EXO:1,LATENCY_PLAYER_UNKNOWN:0};xp[xp.LATENCY_PLAYER_RTSP]="LATENCY_PLAYER_RTSP";
xp[xp.LATENCY_PLAYER_HTML5_INLINE]="LATENCY_PLAYER_HTML5_INLINE";xp[xp.LATENCY_PLAYER_HTML5_FULLSCREEN]="LATENCY_PLAYER_HTML5_FULLSCREEN";xp[xp.LATENCY_PLAYER_HTML5]="LATENCY_PLAYER_HTML5";xp[xp.LATENCY_PLAYER_FRAMEWORK]="LATENCY_PLAYER_FRAMEWORK";xp[xp.LATENCY_PLAYER_FLASH]="LATENCY_PLAYER_FLASH";xp[xp.LATENCY_PLAYER_EXO]="LATENCY_PLAYER_EXO";xp[xp.LATENCY_PLAYER_UNKNOWN]="LATENCY_PLAYER_UNKNOWN";
var yp={LATENCY_AD_BREAK_TYPE_POSTROLL:3,LATENCY_AD_BREAK_TYPE_MIDROLL:2,LATENCY_AD_BREAK_TYPE_PREROLL:1,LATENCY_AD_BREAK_TYPE_UNKNOWN:0};yp[yp.LATENCY_AD_BREAK_TYPE_POSTROLL]="LATENCY_AD_BREAK_TYPE_POSTROLL";yp[yp.LATENCY_AD_BREAK_TYPE_MIDROLL]="LATENCY_AD_BREAK_TYPE_MIDROLL";yp[yp.LATENCY_AD_BREAK_TYPE_PREROLL]="LATENCY_AD_BREAK_TYPE_PREROLL";yp[yp.LATENCY_AD_BREAK_TYPE_UNKNOWN]="LATENCY_AD_BREAK_TYPE_UNKNOWN";var zp={LATENCY_ACTION_ERROR_STARTUP_TIMEOUT:1,LATENCY_ACTION_ERROR_UNSPECIFIED:0};
zp[zp.LATENCY_ACTION_ERROR_STARTUP_TIMEOUT]="LATENCY_ACTION_ERROR_STARTUP_TIMEOUT";zp[zp.LATENCY_ACTION_ERROR_UNSPECIFIED]="LATENCY_ACTION_ERROR_UNSPECIFIED";var Ap={LIVE_STREAM_MODE_WINDOW:5,LIVE_STREAM_MODE_POST:4,LIVE_STREAM_MODE_LP:3,LIVE_STREAM_MODE_LIVE:2,LIVE_STREAM_MODE_DVR:1,LIVE_STREAM_MODE_UNKNOWN:0};Ap[Ap.LIVE_STREAM_MODE_WINDOW]="LIVE_STREAM_MODE_WINDOW";Ap[Ap.LIVE_STREAM_MODE_POST]="LIVE_STREAM_MODE_POST";Ap[Ap.LIVE_STREAM_MODE_LP]="LIVE_STREAM_MODE_LP";
Ap[Ap.LIVE_STREAM_MODE_LIVE]="LIVE_STREAM_MODE_LIVE";Ap[Ap.LIVE_STREAM_MODE_DVR]="LIVE_STREAM_MODE_DVR";Ap[Ap.LIVE_STREAM_MODE_UNKNOWN]="LIVE_STREAM_MODE_UNKNOWN";var Bp={VIDEO_STREAM_TYPE_VOD:3,VIDEO_STREAM_TYPE_DVR:2,VIDEO_STREAM_TYPE_LIVE:1,VIDEO_STREAM_TYPE_UNSPECIFIED:0};Bp[Bp.VIDEO_STREAM_TYPE_VOD]="VIDEO_STREAM_TYPE_VOD";Bp[Bp.VIDEO_STREAM_TYPE_DVR]="VIDEO_STREAM_TYPE_DVR";Bp[Bp.VIDEO_STREAM_TYPE_LIVE]="VIDEO_STREAM_TYPE_LIVE";Bp[Bp.VIDEO_STREAM_TYPE_UNSPECIFIED]="VIDEO_STREAM_TYPE_UNSPECIFIED";
var Cp={YT_IDB_TRANSACTION_TYPE_READ:2,YT_IDB_TRANSACTION_TYPE_WRITE:1,YT_IDB_TRANSACTION_TYPE_UNKNOWN:0};Cp[Cp.YT_IDB_TRANSACTION_TYPE_READ]="YT_IDB_TRANSACTION_TYPE_READ";Cp[Cp.YT_IDB_TRANSACTION_TYPE_WRITE]="YT_IDB_TRANSACTION_TYPE_WRITE";Cp[Cp.YT_IDB_TRANSACTION_TYPE_UNKNOWN]="YT_IDB_TRANSACTION_TYPE_UNKNOWN";var Dp={PLAYER_ROTATION_TYPE_PORTRAIT_TO_FULLSCREEN:2,PLAYER_ROTATION_TYPE_FULLSCREEN_TO_PORTRAIT:1,PLAYER_ROTATION_TYPE_UNKNOWN:0};Dp[Dp.PLAYER_ROTATION_TYPE_PORTRAIT_TO_FULLSCREEN]="PLAYER_ROTATION_TYPE_PORTRAIT_TO_FULLSCREEN";
Dp[Dp.PLAYER_ROTATION_TYPE_FULLSCREEN_TO_PORTRAIT]="PLAYER_ROTATION_TYPE_FULLSCREEN_TO_PORTRAIT";Dp[Dp.PLAYER_ROTATION_TYPE_UNKNOWN]="PLAYER_ROTATION_TYPE_UNKNOWN";var Ep=y.ytLoggingGelSequenceIdObj_||{};z("ytLoggingGelSequenceIdObj_",Ep,void 0);function Fp(a,b,c){c=void 0===c?{}:c;var d=Math.round(c.timestamp||M());G(a,1,d<Number.MAX_SAFE_INTEGER?d:0);var e=Qh();d=new Ig;G(d,1,c.timestamp||!isFinite(e)?-1:e);if(L("log_sequence_info_on_gel_web")&&c.W){e=c.W;var f=nj(e),g=new Hg;G(g,2,f);G(g,1,e);H(d,3,g);c.ub&&delete Ep[c.W]}H(a,33,d);(c.nc?Zi:Ui)({endpoint:"log_event",payload:a,cttAuthInfo:c.cttAuthInfo,xa:c.xa},b)}
;function Gp(a,b){b=void 0===b?{}:b;var c=!1;B("ytLoggingEventsDefaultDisabled",!1)&&fn===fn&&(c=!0);Fp(a,c?null:fn,b)}
;function Hp(a,b,c){var d=new Jg;Sc(d,72,a);c?Fp(d,c,b):Gp(d,b)}
function Ip(a,b,c){var d=new Jg;Sc(d,73,a);c?Fp(d,c,b):Gp(d,b)}
function Jp(a,b,c){var d=new Jg;Sc(d,78,a);c?Fp(d,c,b):Gp(d,b)}
function Kp(a,b,c){var d=new Jg;Sc(d,208,a);c?Fp(d,c,b):Gp(d,b)}
function Lp(a,b,c){var d=new Jg;Sc(d,156,a);c?Fp(d,c,b):Gp(d,b)}
function Mp(a,b,c){var d=new Jg;Sc(d,215,a);c?Fp(d,c,b):Gp(d,b)}
;var Np=y.ytLoggingLatencyUsageStats_||{};z("ytLoggingLatencyUsageStats_",Np,void 0);function Op(){this.h=0}
function Pp(){Op.h||(Op.h=new Op);return Op.h}
Op.prototype.tick=function(a,b,c,d){Qp(this,"tick_"+a+"_"+b)||(c={timestamp:c,cttAuthInfo:d},L("web_csi_via_jspb")?(d=new Gg,G(d,1,a),G(d,2,b),a=new Jg,Sc(a,5,d),Gp(a,c)):fk("latencyActionTicked",{tickName:a,clientActionNonce:b},c))};
Op.prototype.info=function(a,b,c){var d=Object.keys(a).join("");Qp(this,"info_"+d+"_"+b)||(a=Object.assign({},a),a.clientActionNonce=b,fk("latencyActionInfo",a,{cttAuthInfo:c}))};
Op.prototype.jspbInfo=function(a,b,c){for(var d="",e=0;e<a.toJSON().length;e++)void 0!==a.toJSON()[e]&&(d=0===e?d.concat(""+e):d.concat("_"+e));Qp(this,"info_"+d+"_"+b)||(G(a,2,b),b={cttAuthInfo:c},c=new Jg,Sc(c,7,a),Gp(c,b))};
Op.prototype.span=function(a,b,c){var d=Object.keys(a).join("");Qp(this,"span_"+d+"_"+b)||(a.clientActionNonce=b,fk("latencyActionSpan",a,{cttAuthInfo:c}))};
function Qp(a,b){Np[b]=Np[b]||{count:0};var c=Np[b];c.count++;c.time=M();a.h||(a.h=Fh(function(){var d=M(),e;for(e in Np)Np[e]&&6E4<d-Np[e].time&&delete Np[e];a&&(a.h=0)},5E3));
return 5<c.count?(6===c.count&&1>1E5*Math.random()&&(c=new ik("CSI data exceeded logging limit with key",b.split("_")),0<=b.indexOf("plev")||An(c)),!0):!1}
;function Rp(){var a=["ol"];mp("").info.actionType="embed";a&&dh("TIMING_AFT_KEYS",a);dh("TIMING_ACTION","embed");a=B("TIMING_INFO",{});for(var b in a)a.hasOwnProperty(b)&&Sp(b,a[b]);b={isNavigation:!0,actionType:up(eh("TIMING_ACTION"))};if(a=eh("PREVIOUS_ACTION"))b.previousAction=up(a);if(a=B("CLIENT_PROTOCOL"))b.httpProtocol=a;if(a=B("CLIENT_TRANSPORT"))b.transportProtocol=a;(a=Rn())&&"UNDEFINED_CSN"!==a&&(b.clientScreenNonce=a);a=Tp();if(1===a||-1===a)b.isVisible=!0;a=ep();b.loadType="cold";var c=
Zo(),d=ap();d&&(Z("srt",c.responseStart),1!==a.prerender&&Z("_start",d,void 0));a=kp();0<a&&Z("fpt",a);a=Zo();a.isPerformanceNavigationTiming&&Up({performanceNavigationTiming:!0},void 0);Z("nreqs",a.requestStart,void 0);Z("nress",a.responseStart,void 0);Z("nrese",a.responseEnd,void 0);0<a.redirectEnd-a.redirectStart&&(Z("nrs",a.redirectStart,void 0),Z("nre",a.redirectEnd,void 0));0<a.domainLookupEnd-a.domainLookupStart&&(Z("ndnss",a.domainLookupStart,void 0),Z("ndnse",a.domainLookupEnd,void 0));0<
a.connectEnd-a.connectStart&&(Z("ntcps",a.connectStart,void 0),Z("ntcpe",a.connectEnd,void 0));a.secureConnectionStart>=ap()&&0<a.connectEnd-a.secureConnectionStart&&(Z("nstcps",a.secureConnectionStart,void 0),Z("ntcpe",a.connectEnd,void 0));Q&&"getEntriesByType"in Q&&Vp();a=[];if(document.querySelector&&Q&&Q.getEntriesByName)for(var e in Vo)Vo.hasOwnProperty(e)&&(c=Vo[e],Wp(e,c)&&a.push(c));if(0<a.length)for(b.resourceInfo=[],e=q(a),a=e.next();!a.done;a=e.next())b.resourceInfo.push({resourceCache:a.value});
Up(b);e=ep();b=gp();if("cold"===e.yt_lt||"cold"===b.loadType){a=Yo();c=fp();c=c.gelTicks?c.gelTicks:c.gelTicks={};for(var f in a)f in c||Z(f,a[f]);f={};a=!1;c=q(Object.keys(e));for(d=c.next();!d.done;d=c.next())d=d.value,(d=vp(d,e[d]))&&!jp(gp(void 0),d)&&(Lo(b,d),Lo(f,d),a=!0);a&&Up(f)}bp();f=eh("TIMING_ACTION");A("ytglobal.timingready_")&&f&&"_start"in Yo(void 0)&&Xo()&&ip()}
function Sp(a,b,c){null!==b&&(ep(c)[a]=b,(a=vp(a,b,c))&&Up(a,c))}
function Up(a,b){if(L("web_csi_via_jspb")){var c=new Dg,d=Object.keys(a);a=Object.values(a);for(var e=0;e<d.length;e++)switch(d[e]){case "actionType":G(c,1,U[a[e]]);break;case "clientActionNonce":G(c,2,a[e]);break;case "clientScreenNonce":G(c,4,a[e]);break;case "actionVisualElement":H(c,88,a[e]);break;case "loadType":G(c,3,a[e]);break;case "isFirstInstall":G(c,55,a[e]);break;case "networkType":G(c,5,wp[a[e]]);break;case "connectionType":G(c,26,V[a[e]]);break;case "detailedConnectionType":G(c,27,X[a[e]]);
break;case "isVisible":G(c,6,a[e]);break;case "playerType":G(c,7,xp[a[e]]);break;case "clientPlaybackNonce":G(c,8,a[e]);break;case "adClientPlaybackNonce":G(c,28,a[e]);break;case "previousCpn":G(c,77,a[e]);break;case "targetCpn":G(c,76,a[e]);break;case "isMonetized":G(c,9,a[e]);break;case "isPrerollAllowed":G(c,16,a[e]);break;case "isPrerollShown":G(c,17,a[e]);break;case "adType":G(c,12,a[e]);break;case "adTypesAllowed":G(c,36,a[e]);break;case "adNetworks":G(c,37,a[e]);break;case "previousAction":G(c,
13,U[a[e]]);break;case "isRedSubscriber":G(c,14,a[e]);break;case "serverTimeMs":G(c,15,a[e]);break;case "spinnerInfo":H(c,18,a[e]);break;case "videoId":c.setVideoId(a[e]);break;case "adVideoId":G(c,20,a[e]);break;case "targetVideoId":G(c,78,a[e]);break;case "adBreakType":G(c,21,yp[a[e]]);break;case "isNavigation":G(c,25,a[e]);break;case "viewportHeight":G(c,29,a[e]);break;case "viewportWidth":G(c,30,a[e]);break;case "screenHeight":G(c,84,a[e]);break;case "screenWidth":G(c,85,a[e]);break;case "browseId":G(c,
31,a[e]);break;case "isCacheHit":G(c,32,a[e]);break;case "httpProtocol":G(c,33,a[e]);break;case "transportProtocol":G(c,34,a[e]);break;case "searchQuery":G(c,41,a[e]);break;case "isContinuation":G(c,42,a[e]);break;case "availableProcessors":G(c,43,a[e]);break;case "sdk":G(c,44,a[e]);break;case "isLocalStream":G(c,45,a[e]);break;case "navigationRequestedSameUrl":G(c,64,a[e]);break;case "shellStartupDurationMs":G(c,70,a[e]);break;case "appInstallDataAgeMs":G(c,73,a[e]);break;case "latencyActionError":G(c,
71,zp[a[e]]);break;case "actionStep":G(c,79,a[e]);break;case "jsHeapSizeLimit":G(c,80,a[e]);break;case "totalJsHeapSize":G(c,81,a[e]);break;case "usedJsHeapSize":G(c,82,a[e]);break;case "resourceInfo":Uc(c,83,Cg,a[e]);break;case "sourceVideoDurationMs":G(c,90,a[e]);break;case "playerInfo":H(c,22,a[e]);break;case "commentInfo":H(c,23,a[e]);break;case "mdxInfo":H(c,24,a[e]);break;case "watchInfo":H(c,35,a[e]);break;case "adPrebufferedTimeSecs":G(c,39,a[e]);break;case "thumbnailLoadInfo":H(c,40,a[e]);
break;case "creatorInfo":H(c,46,a[e]);break;case "unpluggedInfo":H(c,50,a[e]);break;case "isLivestream":G(c,47,a[e]);break;case "liveStreamMode":G(c,91,Ap[a[e]]);break;case "adCpn2":G(c,48,a[e]);break;case "adDaiDriftMillis":G(c,49,a[e]);break;case "videoStreamType":G(c,53,Bp[a[e]]);break;case "reelInfo":H(c,54,a[e]);break;case "subscriptionsFeedInfo":H(c,72,a[e]);break;case "playbackRequiresTap":G(c,56,a[e]);break;case "requestIds":Uc(c,68,Fg,a[e]);break;case "mediaBrowserActionInfo":H(c,58,a[e]);
break;case "performanceNavigationTiming":G(c,67,a[e]);break;case "musicLoadActionInfo":H(c,69,a[e]);break;case "transactionType":G(c,74,Cp[a[e]]);break;case "shoppingInfo":H(c,75,a[e]);break;case "prefetchInfo":H(c,86,a[e]);break;case "accelerationSession":H(c,87,a[e]);break;case "playerRotationType":G(c,101,Dp[a[e]]);break;case "webInfo":H(c,38,a[e]);break;case "tvInfo":H(c,51,a[e]);break;case "kabukiInfo":H(c,52,a[e]);break;case "mwebInfo":H(c,59,a[e]);break;case "musicInfo":H(c,65,a[e]);break;
case "allowedPreroll":G(c,10,a[e]);break;case "shownPreroll":G(c,11,a[e]);break;case "getHomeRequestId":G(c,57,a[e]);break;case "getSearchRequestId":G(c,60,a[e]);break;case "getPlayerRequestId":G(c,61,a[e]);break;case "getWatchNextRequestId":G(c,62,a[e]);break;case "getBrowseRequestId":G(c,63,a[e]);break;case "getLibraryRequestId":G(c,66,a[e])}a=fp(b);a.jspbInfos||(a.jspbInfos=[]);a.jspbInfos.push(c);mp(b||"").jspbInfo.push(c);a=hp(b);b=cp(b).cttAuthInfo;Pp().jspbInfo(c,a,b)}else c=mp(b||""),Lo(c.info,
a),Lo(gp(b),a),c=hp(b),b=cp(b).cttAuthInfo,Pp().info(a,c,b)}
function Z(a,b,c){if(!b&&"_"!==a[0]){var d=a;Q.mark&&(0==d.lastIndexOf("mark_",0)||(d="mark_"+d),c&&(d+=" ("+c+")"),Q.mark(d))}mp(c||"").tick[a]=b||M();d=fp(c);d.gelTicks&&(d.gelTicks[a]=!0);d=Yo(c);var e=b||M();d[a]=e;e=hp(c);var f=cp(c).cttAuthInfo;if("_start"===a){var g=Pp();Qp(g,"baseline_"+e)||(b={timestamp:b,cttAuthInfo:f},L("web_csi_via_jspb")?(f=new Bg,G(f,1,e),e=new Jg,Sc(e,6,f),Gp(e,b)):fk("latencyActionBaselined",{clientActionNonce:e},b))}else Pp().tick(a,e,b,f);ip(c);return d[a]}
function Xp(){var a=hp(void 0);requestAnimationFrame(function(){setTimeout(function(){a===hp(void 0)&&Z("ol",void 0,void 0)},0)})}
function Tp(){var a=document;if("visibilityState"in a)a=a.visibilityState;else{var b=mh+"VisibilityState";a=b in a?a[b]:void 0}switch(a){case "hidden":return 0;case "visible":return 1;case "prerender":return 2;case "unloaded":return 3;default:return-1}}
function Wp(a,b){a=document.querySelector(a);if(!a)return!1;var c="",d=a.nodeName;"SCRIPT"===d?(c=a.src,c||(c=a.getAttribute("data-timing-href"))&&(c=window.location.protocol+c)):"LINK"===d&&(c=a.href);Tb()&&a.setAttribute("nonce",Tb());return c?(a=Q.getEntriesByName(c))&&a[0]&&(a=a[0],c=ap(),Z("rsf_"+b,c+Math.round(a.fetchStart)),Z("rse_"+b,c+Math.round(a.responseEnd)),void 0!==a.transferSize&&0===a.transferSize)?!0:!1:!1}
function Vp(){var a=window.location.protocol,b=Q.getEntriesByType("resource");b=db(b,function(c){return 0===c.name.indexOf(a+"//fonts.gstatic.com/s/")});
(b=fb(b,function(c,d){return d.duration>c.duration?d:c},{duration:0}))&&0<b.startTime&&0<b.responseEnd&&(Z("wffs",$o(b.startTime)),Z("wffe",$o(b.responseEnd)))}
var Yp=window;Yp.ytcsi&&(Yp.ytcsi.info=Sp,Yp.ytcsi.tick=Z);var Zp=["consistency","mss","client_location","entities","store"];function $p(a,b,c,d,e){this.o=a;this.I=b;this.l=c;this.m=d;this.j=e;this.i=void 0;this.h=new Map;a.Ba||(a.Ba={});a.Ba=Object.assign(Object.assign({},Ko),a.Ba)}
function aq(a,b,c,d,e){if(void 0!==$p.h){if(d=$p.h,a=[a!==d.o,b!==d.I,c!==d.l,!1,!1,void 0!==d.i],a.some(function(f){return f}))throw new ik("InnerTubeTransportService is already initialized",a);
}else $p.h=new $p(a,b,c,d,e)}
function bq(){var a=$p.h,b={signalServiceEndpoint:{signal:"GET_DATASYNC_IDS"}};var c=void 0===c?qo:c;var d=Bo(b,a.o);if(!d)return yf(new ik("Error: No request builder found for command.",b));var e=d.o(b,void 0,c);return e?new tf(function(f){var g,h,k;return w(function(m){if(1==m.h)return h="cors"===(null===(g=e.ta)||void 0===g?void 0:g.mode)?"cors":void 0,v(m,cq(a,e.config,h),2);k=m.i;f(dq(a,e,k));m.h=0})}):yf(new ik("Error: Failed to build request for command.",b))}
function dq(a,b,c){var d,e,f,g,h,k,m,n,t,x,u,C,D,K,N,S,W,Qa,zb;return w(function(P){switch(P.h){case 1:P.s(2);break;case 3:if((m=P.i)&&!m.isExpired())return P.return(Promise.resolve(m.h()));case 2:if((n=null===(d=b.config)||void 0===d?void 0:d.Bo)&&a.h.has(n)&&L("web_memoize_inflight_requests"))return P.return(a.h.get(n));if(null===(e=null===b||void 0===b?void 0:b.aa)||void 0===e?0:e.context)for(t=q([]),x=t.next();!x.done;x=t.next())u=x.value,u.zo(b.aa.context);if(null===(f=a.i)||void 0===f?0:f.l(b.input,
b.aa))return P.return(a.i.j(b.input,b.aa));C=JSON.stringify(b.aa);b.ta=Object.assign(Object.assign({},b.ta),{headers:c});D=Object.assign({},b.ta);"POST"===b.ta.method&&(D=Object.assign(Object.assign({},D),{body:C}));(null===(g=b.config)||void 0===g?0:g.jc)&&Z(b.config.jc);K=a.I.fetch(b.input,D,b.config);n&&a.h.set(n,K);return v(P,K,4);case 4:N=P.i;n&&a.h.has(n)&&a.h.delete(n);(null===(h=b.config)||void 0===h?0:h.kc)&&Z(b.config.kc);if(N||null===(k=a.i)||void 0===k||!k.h(b.input,b.aa)){P.s(5);break}return v(P,
a.i.i(b.input,b.aa),6);case 6:N=P.i;case 5:if(L("web_ordered_response_processors")&&N&&a.j)for(S=q(Zp),x=S.next();!x.done;x=S.next())W=x.value,a.j[W]&&a.j[W].handleResponse(N,b);else if(N&&a.m)for(Qa=q(a.m),x=Qa.next();!x.done;x=Qa.next())zb=x.value,zb.handleResponse(N,b);return P.return(N)}})}
function cq(a,b,c){return w(function(d){if(a.l.yc){var e=d.return,f,g=null===(f=null===b||void 0===b?void 0:b.Ia)||void 0===f?void 0:f.sessionIndex;f=po({sessionIndex:g});f=Object.assign(Object.assign({},eq(c)),f);d=e.call(d,f)}else d=d.return(fq(b,c));return d})}
function fq(a,b){var c,d,e;return w(function(f){if(1==f.h){d=null===(c=null===a||void 0===a?void 0:a.Ia)||void 0===c?void 0:c.sessionIndex;var g=po({sessionIndex:d});if(!(g instanceof tf)){var h=new tf(Ia);uf(h,2,g);g=h}return v(f,g,2)}e=f.i;return f.return(Promise.resolve(Object.assign(Object.assign({},eq(b)),e)))})}
function eq(a){var b={"Content-Type":"application/json"};L("enable_web_eom_visitor_data")&&B("EOM_VISITOR_DATA")?b["X-Goog-EOM-Visitor-Id"]=B("EOM_VISITOR_DATA"):B("VISITOR_DATA")&&(b["X-Goog-Visitor-Id"]=B("VISITOR_DATA"));"cors"!==a&&((a=B("INNERTUBE_CONTEXT_CLIENT_NAME"))&&(b["X-Youtube-Client-Name"]=a),(a=B("INNERTUBE_CONTEXT_CLIENT_VERSION"))&&(b["X-Youtube-Client-Version"]=a),(a=B("CHROME_CONNECTED_HEADER"))&&(b["X-Youtube-Chrome-Connected"]=a),L("forward_domain_admin_state_on_embeds")&&(a=
B("DOMAIN_ADMIN_STATE"))&&(b["X-Youtube-Domain-Admin-State"]=a));return b}
;var gq=["share/get_web_player_share_panel"],hq=["feedback"],iq=["notification/modify_channel_preference"],jq=["browse/edit_playlist"],kq=["subscription/subscribe"],lq=["subscription/unsubscribe"];function mq(){}
r(mq,Ho);mq.prototype.j=function(){return kq};
mq.prototype.h=function(a){return a.subscribeEndpoint};
mq.prototype.i=function(a,b,c){c=void 0===c?{}:c;b.channelIds&&(a.channelIds=b.channelIds);b.siloName&&(a.siloName=b.siloName);b.params&&(a.params=b.params);c.botguardResponse&&(a.botguardResponse=c.botguardResponse);c.feature&&(a.clientFeature=c.feature)};
da.Object.defineProperties(mq.prototype,{l:{configurable:!0,enumerable:!0,get:function(){return!0}}});function nq(){}
r(nq,Ho);nq.prototype.j=function(){return lq};
nq.prototype.h=function(a){return a.unsubscribeEndpoint};
nq.prototype.i=function(a,b){b.channelIds&&(a.channelIds=b.channelIds);b.siloName&&(a.siloName=b.siloName);b.params&&(a.params=b.params)};
da.Object.defineProperties(nq.prototype,{l:{configurable:!0,enumerable:!0,get:function(){return!0}}});function oq(){}
r(oq,Ho);oq.prototype.j=function(){return hq};
oq.prototype.h=function(a){return a.feedbackEndpoint};
oq.prototype.i=function(a,b,c){a.feedbackTokens=[];b.feedbackToken&&a.feedbackTokens.push(b.feedbackToken);if(b=b.cpn||c.cpn)a.feedbackContext={cpn:b};a.isFeedbackTokenUnencrypted=!!c.is_feedback_token_unencrypted;a.shouldMerge=!1;c.extra_feedback_tokens&&(a.shouldMerge=!0,a.feedbackTokens=a.feedbackTokens.concat(c.extra_feedback_tokens))};
da.Object.defineProperties(oq.prototype,{l:{configurable:!0,enumerable:!0,get:function(){return!0}}});function pq(){}
r(pq,Ho);pq.prototype.j=function(){return iq};
pq.prototype.h=function(a){return a.modifyChannelNotificationPreferenceEndpoint};
pq.prototype.i=function(a,b){b.params&&(a.params=b.params);b.secondaryParams&&(a.secondaryParams=b.secondaryParams)};function qq(){}
r(qq,Ho);qq.prototype.j=function(){return jq};
qq.prototype.h=function(a){return a.playlistEditEndpoint};
qq.prototype.i=function(a,b){b.actions&&(a.actions=b.actions);b.params&&(a.params=b.params);b.playlistId&&(a.playlistId=b.playlistId)};function rq(){}
r(rq,Ho);rq.prototype.j=function(){return gq};
rq.prototype.h=function(a){return a.webPlayerShareEntityServiceEndpoint};
rq.prototype.i=function(a,b,c){c=void 0===c?{}:c;b.serializedShareEntity&&(a.serializedSharedEntity=b.serializedShareEntity);c.includeListId&&(a.includeListId=!0)};function sq(){}
sq.prototype.fetch=function(a,b){var c=this,d,e,f;return w(function(g){if(1==g.h){d=new window.Request(a,b);if(L("web_fetch_promise_cleanup_killswitch"))return g.return(Promise.resolve(fetch(d).then(function(h){return c.handleResponse(h)}).catch(function(h){An(h)})));
sa(g,3);return v(g,fetch(d),5)}if(3!=g.h)return e=g.i,g.return(c.handleResponse(e));f=ua(g);An(f);return g.return(void 0)})};
sq.prototype.handleResponse=function(a){var b=a.text().then(function(c){return JSON.parse(c.replace(")]}'",""))});
a.redirected||a.ok||(b=b.then(function(c){An(new ik("Error: API fetch failed",a.status,a.url,c));return Object.assign(Object.assign({},c),{errorMetadata:{status:a.status}})}));
return b};var tq;function uq(a){Kl.call(this,1,arguments);this.csn=a}
r(uq,Kl);var Tl=new Ll("screen-created",uq),vq=[],xq=wq,yq=0;function zq(a,b,c,d,e,f,g){function h(){An(new ik("newScreen() parent element does not have a VE - rootVe",b))}
var k=xq();f=new Kn({veType:b,youtubeData:f,jspbYoutubeData:void 0});e={cttAuthInfo:e,W:k};if(L("il_via_jspb")){var m=new rg;m.X(k);sg(m,f.getAsJspb());c&&c.visualElement?(f=new tg,c.clientScreenNonce&&G(f,2,c.clientScreenNonce),ug(f,c.visualElement.getAsJspb()),g&&G(f,4,qg[g]),H(m,5,f)):c&&h();d&&G(m,3,d);Lp(m,e,a)}else f={csn:k,pageVe:f.getAsJson()},c&&c.visualElement?(f.implicitGesture={parentCsn:c.clientScreenNonce,gesturedVe:c.visualElement.getAsJson()},g&&(f.implicitGesture.gestureType=g)):
c&&h(),d&&(f.cloneCsn=d),a?mj("screenCreated",f,a,e):fk("screenCreated",f,e);Ql(Tl,new uq(k));return k}
function Aq(a,b,c,d){var e=d.filter(function(k){k.csn!==b?(k.csn=b,k=!0):k=!1;return k}),f={cttAuthInfo:Tn(b),
W:b};d=q(d);for(var g=d.next();!g.done;g=d.next())g=g.value.getAsJson(),(nb(g)||!g.trackingParams&&!g.veType)&&An(Error("Child VE logged with no data"));if(L("il_via_jspb")){var h=new vg;h.X(b);xg(h,c.getAsJspb());eb(e,function(k){k=k.getAsJspb();Uc(h,3,pg,k)});
"UNDEFINED_CSN"==b?Bq("visualElementAttached",h,f):Mp(h,f,a)}else c={csn:b,parentVe:c.getAsJson(),childVes:eb(e,function(k){return k.getAsJson()})},"UNDEFINED_CSN"==b?Bq("visualElementAttached",c,f):a?mj("visualElementAttached",c,a,f):fk("visualElementAttached",c,f)}
function wq(){for(var a=Math.random()+"",b=[],c=0,d=0;d<a.length;d++){var e=a.charCodeAt(d);255<e&&(b[c++]=e&255,e>>=8);b[c++]=e}return zc(b,3)}
function Bq(a,b,c){vq.push({payloadName:a,payload:b,options:c});yq||(yq=Ul())}
function Vl(a){if(vq){for(var b=q(vq),c=b.next();!c.done;c=b.next())if(c=c.value,c.payload)if(L("il_via_jspb"))switch(c.payload.X(a.csn),c.payloadName){case "screenCreated":Lp(c.payload,c.options);break;case "visualElementAttached":Mp(c.payload,c.options);break;case "visualElementShown":Hp(c.payload,c.options);break;case "visualElementHidden":Ip(c.payload,c.options);break;case "visualElementGestured":Jp(c.payload,c.options);break;case "visualElementStateChanged":Kp(c.payload,c.options);break;default:An(new ik("flushQueue unable to map payloadName to JSPB setter"))}else c.payload.csn=
a.csn,mj(c.payloadName,c.payload,null,c.options);vq.length=0}yq=0}
;function Cq(){this.i=new Set;this.h=new Set;this.j=new Map}
Cq.prototype.clear=function(){this.i.clear();this.h.clear();this.j.clear()};
Ja(Cq);function Dq(){this.o=[];this.D=[];this.h=[];this.l=[];this.m=[];this.i=new Set;this.u=new Map}
function Eq(a,b,c){c=void 0===c?0:c;b.then(function(d){var e,f;a.i.has(c)&&a.j&&a.j();var g=Rn(c),h=Pn(c);g&&h&&((null===(e=null===d||void 0===d?void 0:d.response)||void 0===e?0:e.trackingParams)&&Aq(a.client,g,h,[Ln(d.response.trackingParams)]),(null===(f=null===d||void 0===d?void 0:d.playerResponse)||void 0===f?0:f.trackingParams)&&Aq(a.client,g,h,[Ln(d.playerResponse.trackingParams)]))})}
function Fq(a,b,c,d){d=void 0===d?0:d;if(a.i.has(d))a.o.push([b,c]);else{var e=Rn(d);c=c||Pn(d);e&&c&&Aq(a.client,e,c,[b])}}
Dq.prototype.clickCommand=function(a,b,c){var d=a.clickTrackingParams;c=void 0===c?0:c;if(d)if(c=Rn(void 0===c?0:c)){a=this.client;var e=Ln(d);var f="INTERACTION_LOGGING_GESTURE_TYPE_GENERIC_CLICK";d={cttAuthInfo:Tn(c),W:c};if(L("il_via_jspb")){var g=new yg;g.X(c);e=e.getAsJspb();H(g,2,e);G(g,4,qg[f]);b&&H(g,3,void 0);"UNDEFINED_CSN"==c?Bq("visualElementGestured",g,d):Jp(g,d,a)}else f={csn:c,ve:e.getAsJson(),gestureType:f},b&&(f.clientData=b),"UNDEFINED_CSN"==c?Bq("visualElementGestured",f,d):a?mj("visualElementGestured",
f,a,d):fk("visualElementGestured",f,d);b=!0}else b=!1;else b=!1;return b};
function Gq(a,b,c){c=void 0===c?{}:c;a.i.add(c.layer||0);a.j=function(){Hq(a,b,c);var f=Pn(c.layer);if(f){for(var g=q(a.o),h=g.next();!h.done;h=g.next())h=h.value,Fq(a,h[0],h[1]||f,c.layer);f=q(a.D);for(g=f.next();!g.done;g=f.next()){var k=g.value;g=void 0;g=void 0===g?0:g;h=Rn(g);var m=k[0]||Pn(g);if(h&&m){g=a.client;var n=k[1];k={cttAuthInfo:Tn(h),W:h};L("il_via_jspb")?(n=new Ag,n.X(h),m=m.getAsJspb(),H(n,2,m),"UNDEFINED_CSN"==h?Bq("visualElementStateChanged",n,k):Kp(n,k,g)):(m={csn:h,ve:m.getAsJson(),
clientData:n},"UNDEFINED_CSN"==h?Bq("visualElementStateChanged",m,k):g?mj("visualElementStateChanged",m,g,k):fk("visualElementStateChanged",m,k))}}}};
Rn(c.layer)||a.j();if(c.sb)for(var d=q(c.sb),e=d.next();!e.done;e=d.next())Eq(a,e.value,c.layer);else zn(Error("Delayed screen needs a data promise."))}
function Hq(a,b,c){c=void 0===c?{}:c;c.layer||(c.layer=0);var d=void 0!==c.ec?c.ec:c.layer;var e=Rn(d);d=Pn(d);var f;d&&(void 0!==c.parentCsn?f={clientScreenNonce:c.parentCsn,visualElement:d}:e&&"UNDEFINED_CSN"!==e&&(f={clientScreenNonce:e,visualElement:d}));var g,h=B("EVENT_ID");"UNDEFINED_CSN"===e&&h&&(g={servletData:{serializedServletEventId:h}});try{var k=zq(a.client,b,f,c.rb,c.cttAuthInfo,g,c.to)}catch(m){Bn(m,{Co:b,rootVe:d,parentVisualElement:void 0,qo:e,yo:f,rb:c.rb});zn(m);return}Un(k,b,
c.layer,c.cttAuthInfo);if((b=e&&"UNDEFINED_CSN"!==e&&d)&&!(b=L("screen_manager_skip_hide_killswitch"))){a:{b=q(Object.values(Jn));for(f=b.next();!f.done;f=b.next())if(Rn(f.value)==e){b=!0;break a}b=!1}b=!b}b&&(b=a.client,g=!0,h=(g=void 0===g?!1:g)?16:8,f={cttAuthInfo:Tn(e),W:e,ub:g},L("il_via_jspb")?(h=new zg,h.X(e),d=d.getAsJspb(),H(h,2,d),G(h,4,g?16:8),"UNDEFINED_CSN"==e?Bq("visualElementHidden",h,f):Ip(h,f,b)):(d={csn:e,ve:d.getAsJson(),eventType:h},"UNDEFINED_CSN"==e?Bq("visualElementHidden",
d,f):b?mj("visualElementHidden",d,b,f):fk("visualElementHidden",d,f)));a.h[a.h.length-1]&&!a.h[a.h.length-1].csn&&(a.h[a.h.length-1].csn=k||"");Up({clientScreenNonce:k});Cq.getInstance().clear();d=Pn(c.layer);e&&"UNDEFINED_CSN"!==e&&d&&(L("web_mark_root_visible")||L("music_web_mark_root_visible"))&&(e=k,k={cttAuthInfo:Tn(e),W:e},L("il_via_jspb")?(b=new zg,b.X(e),f=d.getAsJspb(),H(b,2,f),G(b,4,1),"UNDEFINED_CSN"==e?Bq("visualElementShown",b,k):Hp(b,k,void 0)):(b={csn:e,ve:d.getAsJson(),eventType:1},
"UNDEFINED_CSN"==e?Bq("visualElementShown",b,k):fk("visualElementShown",b,k)));a.i.delete(c.layer||0);a.j=void 0;e=q(a.u);for(k=e.next();!k.done;k=e.next())b=q(k.value),k=b.next().value,b=b.next().value,b.has(c.layer)&&d&&Fq(a,k,d,c.layer);for(c=0;c<a.l.length;c++){e=a.l[c];try{e()}catch(m){zn(m)}}for(c=a.l.length=0;c<a.m.length;c++){e=a.m[c];try{e()}catch(m){zn(m)}}}
;function Iq(){var a;return w(function(b){if(1==b.h)return v(b,bq(),2);if(a=b.i)return a.errorMetadata?(zn(Error("Datasync IDs fetch responded with "+a.errorMetadata.code+": "+a.error)),b.return(void 0)):b.return(a.ro);An(Error("Network request to get Datasync IDs failed."));return b.return(void 0)})}
;var Jq=y.caches,Kq;function Lq(a){var b=a.indexOf(":");return-1===b?{Cb:a}:{Cb:a.substring(0,b),datasyncId:a.substring(b+1)}}
function Mq(){return w(function(a){if(void 0!==Kq)return a.return(Kq);Kq=new Promise(function(b){var c;return w(function(d){switch(d.h){case 1:return sa(d,2),v(d,Jq.open("test-only"),4);case 4:return v(d,Jq.delete("test-only"),5);case 5:ta(d,3);break;case 2:if(c=ua(d),c instanceof Error&&"SecurityError"===c.name)return b(!1),d.return();case 3:b("caches"in window),d.h=0}})});
return a.return(Kq)})}
function Nq(a){var b,c,d,e,f,g,h;w(function(k){if(1==k.h)return v(k,Mq(),2);if(3!=k.h){if(!k.i)return k.return(!1);b=[];return v(k,Jq.keys(),3)}c=k.i;d=q(c);for(e=d.next();!e.done;e=d.next())f=e.value,g=Lq(f),h=g.datasyncId,!h||a.includes(h)||b.push(Jq.delete(f));return k.return(Promise.all(b).then(function(m){return m.some(function(n){return n})}))})}
;function Oq(){Iq().then(function(a){if(a&&(tl(a),Nq(a),L("clear_user_partitioned_ls"))){var b=void 0===b?{}:b;"_start"in Yo("cupls")&&Z("aa",void 0,"cupls");var c=np();c.cupls&&delete c.cupls;var d={timerName:"cupls",info:{},tick:{},span:{},jspbInfo:[]};lp().push(d);c.cupls=d;dp("cupls");Wo();cp("cupls").useGel=!0;mp("cupls").info.actionType="cupls";b.cttAuthInfo&&(cp("cupls").cttAuthInfo=b.cttAuthInfo);dh("cuplsTIMING_ACTION","cupls");Z("_start",b.startTime,"cupls");b={actionType:up("cupls")};(c=
Rn())&&"UNDEFINED_CSN"!==c&&(b.clientScreenNonce=c);Up(b,"cupls");bp("cupls");Z("cuplss",void 0,"cupls");try{try{var e=!!self.localStorage}catch(t){e=!1}if(e)for(var f=Object.keys(window.localStorage),g=q(f),h=g.next();!h.done;h=g.next()){var k=h.value;var m=k.match(/(.*)::.*::.*/);var n=null!==m?m[1]:void 0;e=n;void 0===e||a.includes(e)||self.localStorage.removeItem(k)}Z("cuplsc",void 0,"cupls")}catch(t){zn(t),Z("cuplse",void 0,"cupls")}}})}
function Pq(){var a=new Em;Lh.M(function(){a.G()?Oq():a.i.add("publicytnetworkstatus-online",Oq,!0,void 0,void 0)})}
;function Qq(a){a&&(a.dataset?a.dataset[Rq("loaded")]="true":a.setAttribute("data-loaded","true"))}
function Sq(a,b){return a?a.dataset?a.dataset[Rq(b)]:a.getAttribute("data-"+b):null}
var Tq={};function Rq(a){return Tq[a]||(Tq[a]=String(a).replace(/\-([a-z])/g,function(b,c){return c.toUpperCase()}))}
;var Uq=/\.vflset|-vfl[a-zA-Z0-9_+=-]+/,Vq=/-[a-zA-Z]{2,3}_[a-zA-Z]{2,3}(?=(\/|$))/;function Wq(a,b,c){c=void 0===c?null:c;if(window.spf&&spf.script){c="";if(a){var d=a.indexOf("jsbin/"),e=a.lastIndexOf(".js"),f=d+6;-1<d&&-1<e&&e>f&&(c=a.substring(f,e),c=c.replace(Uq,""),c=c.replace(Vq,""),c=c.replace("debug-",""),c=c.replace("tracing-",""))}spf.script.load(a,c,b)}else Xq(a,b,c)}
function Xq(a,b,c){c=void 0===c?null:c;var d=Yq(a),e=document.getElementById(d),f=e&&Sq(e,"loaded"),g=e&&!f;f?b&&b():(b&&(f=zi(d,b),b=""+Na(b),Zq[b]=f),g||(e=$q(a,d,function(){Sq(e,"loaded")||(Qq(e),Ci(d),Bh(Ua(Di,d),0))},c)))}
function $q(a,b,c,d){d=void 0===d?null:d;var e=jd("SCRIPT");e.id=b;e.onload=function(){c&&setTimeout(c,0)};
e.onreadystatechange=function(){switch(e.readyState){case "loaded":case "complete":e.onload()}};
d&&e.setAttribute("nonce",d);fd(e,kf(a));a=document.getElementsByTagName("head")[0]||document.body;a.insertBefore(e,a.firstChild);return e}
function ar(a){a=Yq(a);var b=document.getElementById(a);b&&(Di(a),b.parentNode.removeChild(b))}
function br(a,b){a&&b&&(a=""+Na(b),(a=Zq[a])&&Bi(a))}
function Yq(a){var b=document.createElement("a");Qb(b,a);a=b.href.replace(/^[a-zA-Z]+:\/\//,"//");return"js-"+Vb(a)}
var Zq={};var cr=[],dr=!1;function er(){if(!L("disable_biscotti_fetch_for_ad_blocker_detection")&&!L("disable_biscotti_fetch_entirely_for_all_web_clients")&&fo()&&"1"!=pb()){var a=function(){dr=!0;"google_ad_status"in window?dh("DCLKSTAT",1):dh("DCLKSTAT",2)};
try{Wq("//static.doubleclick.net/instream/ad_status.js",a)}catch(b){}cr.push(Lh.M(function(){if(!(dr||"google_ad_status"in window)){try{br("//static.doubleclick.net/instream/ad_status.js",a)}catch(b){}dr=!0;dh("DCLKSTAT",3)}},5E3))}}
function fr(){var a=Number(B("DCLKSTAT",0));return isNaN(a)?0:a}
;function gr(){this.state=1;this.h=null}
l=gr.prototype;
l.initialize=function(a,b,c){var d,e;if(a.program){var f=null!==(d=a.interpreterScript)&&void 0!==d?d:null,g=null!==(e=a.interpreterUrl)&&void 0!==e?e:null;if(a.interpreterSafeScript){f=a.interpreterSafeScript;Ab("From proto message. b/166824318");f=f.privateDoNotAccessOrElseSafeScriptWrappedValue||"";var h=wb();f=h?h.createScript(f):f;f=(new Cb(f)).toString()}a.interpreterSafeUrl&&(g=a.interpreterSafeUrl,Ab("From proto message. b/166824318"),g=Gb(g.privateDoNotAccessOrElseTrustedResourceUrlWrappedValue||"").toString());
hr(this,f,g,a.program,b,c)}else An(Error("Cannot initialize botguard without program"))};
function hr(a,b,c,d,e,f){c?(a.state=2,Wq(c,function(){var g=0<=c.indexOf("/th/");(g?window.trayride:window.botguard)?ir(a,d,g,e):(a.state=3,ar(c),An(new ik("Unable to load Botguard","from "+c)))},f)):b&&(f=jd("SCRIPT"),f.textContent=b,f.nonce=Tb(),document.head.appendChild(f),document.head.removeChild(f),((b=b.includes("trayride"))?window.trayride:window.botguard)?ir(a,d,b,e):(a.state=4,An(Error("Unable to load Botguard from JS"))))}
l.isInitialized=function(){return!!this.h};
l.getState=function(){return this.state};
function ir(a,b,c,d){var e,f;a.state=5;if(L("use_bg_facade"))if(c=c?"trayride":"botguard",window[c])try{var g=new Zc({program:b,globalName:c});g.vc.then(function(){a.state=6;d&&d(b)});
jr(a,g)}catch(h){h instanceof Error&&(a.state=7,An(h))}else a.state=7,An(Error("VM not loaded, cannot start"));else if(g=c?null===(e=window.trayride)||void 0===e?void 0:e.ad:null===(f=window.botguard)||void 0===f?void 0:f.bg){try{jr(a,new g(b)),a.state=6}catch(h){a.state=7,h instanceof Error&&An(h)}d&&d(b)}else a.state=7,An(Error("Failed to finish initializing VM"))}
l.invoke=function(a){a=void 0===a?{}:a;if(this.h){if(this.h.Jb)return this.h.Jb({qb:a});if(this.h.hot)return this.h.hot(void 0,void 0,a);if(this.h.invoke)return this.h.invoke(void 0,void 0,a);An(Error("VM has unknown interface"))}return null};
l.dispose=function(){jr(this,null);this.state=8};
function jr(a,b){Kd(a.h);a.h=b}
;var kr=new gr;function lr(){return kr.isInitialized()}
function mr(a){a=void 0===a?{}:a;return kr.invoke(a)}
;function nr(a){var b=this;var c=void 0===c?0:c;var d=void 0===d?Kh():d;this.l=c;this.j=d;this.i=new Yc;this.h=a;a={};c=q(this.h.entries());for(d=c.next();!d.done;a={va:a.va,Da:a.Da},d=c.next()){var e=q(d.value);d=e.next().value;e=e.next().value;a.Da=d;a.va=e;d=function(f){return function(){f.va.fb();b.h[f.Da].Ta=!0;b.h.every(function(g){return!0===g.Ta})&&b.i.resolve()}}(a);
e=Gh(d,or(this,a.va));this.h[a.Da]=Object.assign(Object.assign({},a.va),{fb:d,Oa:e})}}
function pr(a){var b=Array.from(a.h.keys()).sort(function(d,e){return or(a,a.h[e])-or(a,a.h[d])});
b=q(b);for(var c=b.next();!c.done;c=b.next())c=a.h[c.value],void 0===c.Oa||c.Ta||(a.j.U(c.Oa),Gh(c.fb,10))}
nr.prototype.cancel=function(){for(var a=q(this.h),b=a.next();!b.done;b=a.next())b=b.value,void 0===b.Oa||b.Ta||this.j.U(b.Oa),b.Ta=!0;this.i.resolve()};
function or(a,b){var c;return null!==(c=b.priority)&&void 0!==c?c:a.l}
;function qr(a){this.state=a;this.plugins=[];this.m=void 0}
qr.prototype.install=function(){this.plugins.push.apply(this.plugins,ha(Da.apply(0,arguments)))};
qr.prototype.transition=function(a,b){var c=this,d=this.D.find(function(f){return f.from===c.state&&f.B===a});
if(d){this.j&&(pr(this.j),this.j=void 0);this.state=a;d=d.action.bind(this);var e=this.plugins.filter(function(f){return f[a]}).map(function(f){return f[a]});
d(rr(this,e,this.m),b)}else throw Error("no transition specified from "+this.state+" to "+a);};
function rr(a,b,c){return function(){var d=Da.apply(0,arguments),e=b.filter(function(k){var m;return 10===(null!==(m=null!==c&&void 0!==c?c:k.priority)&&void 0!==m?m:0)}),f=b.filter(function(k){var m;
return 10!==(null!==(m=null!==c&&void 0!==c?c:k.priority)&&void 0!==m?m:0)});
Kh();var g={};e=q(e);for(var h=e.next();!h.done;g={Ea:g.Ea},h=e.next())g.Ea=h.value,Ih(function(k){return function(){k.Ea.la.apply(k.Ea,ha(d))}}(g));
f=f.map(function(k){var m;return{fb:function(){k.la.apply(k,ha(d))},
priority:null!==(m=null!==c&&void 0!==c?c:k.priority)&&void 0!==m?m:0}});
f.length&&(a.j=new nr(f))}}
da.Object.defineProperties(qr.prototype,{currentState:{configurable:!0,enumerable:!0,get:function(){return this.state}}});function sr(a){qr.call(this,void 0===a?"document_active":a);var b=this;this.m=10;this.h=new Map;this.D=[{from:"document_active",B:"document_disposed_preventable",action:this.u},{from:"document_active",B:"document_disposed",action:this.l},{from:"document_disposed_preventable",B:"document_disposed",action:this.l},{from:"document_disposed_preventable",B:"flush_logs",action:this.o},{from:"document_disposed_preventable",B:"document_active",action:this.i},{from:"document_disposed",B:"flush_logs",action:this.o},
{from:"document_disposed",B:"document_active",action:this.i},{from:"document_disposed",B:"document_disposed",action:function(){}},
{from:"flush_logs",B:"document_active",action:this.i}];window.addEventListener("pagehide",function(c){b.transition("document_disposed",c)});
window.addEventListener("beforeunload",function(c){b.transition("document_disposed_preventable",c)})}
r(sr,qr);sr.prototype.u=function(a,b){if(!this.h.get("document_disposed_preventable")&&(a(b),(null===b||void 0===b?0:b.defaultPrevented)||(null===b||void 0===b?0:b.returnValue))){b.returnValue||(b.returnValue=!0);b.defaultPrevented||b.preventDefault();this.h=new Map;this.transition("document_active");return}this.h.set("document_disposed_preventable",!0);this.h.get("document_disposed")?this.transition("flush_logs"):this.transition("document_disposed")};
sr.prototype.l=function(a,b){this.h.get("document_disposed")?this.transition("document_active"):(a(b),this.h.set("document_disposed",!0),this.transition("flush_logs"))};
sr.prototype.o=function(a,b){a(b);this.transition("document_active")};
sr.prototype.i=function(){this.h=new Map};function tr(a){qr.call(this,void 0===a?"document_visibility_unknown":a);var b=this;this.D=[{from:"document_visibility_unknown",B:"document_visible",action:this.i},{from:"document_visibility_unknown",B:"document_hidden",action:this.h},{from:"document_visibility_unknown",B:"document_foregrounded",action:this.o},{from:"document_visibility_unknown",B:"document_backgrounded",action:this.l},{from:"document_visible",B:"document_hidden",action:this.h},{from:"document_visible",B:"document_foregrounded",action:this.o},
{from:"document_visible",B:"document_visible",action:this.i},{from:"document_foregrounded",B:"document_visible",action:this.i},{from:"document_foregrounded",B:"document_hidden",action:this.h},{from:"document_foregrounded",B:"document_foregrounded",action:this.o},{from:"document_hidden",B:"document_visible",action:this.i},{from:"document_hidden",B:"document_backgrounded",action:this.l},{from:"document_hidden",B:"document_hidden",action:this.h},{from:"document_backgrounded",B:"document_hidden",action:this.h},
{from:"document_backgrounded",B:"document_backgrounded",action:this.l},{from:"document_backgrounded",B:"document_visible",action:this.i}];document.addEventListener("visibilitychange",function(c){"visible"===document.visibilityState?b.transition("document_visible",c):b.transition("document_hidden",c)});
L("visibility_lifecycles_dynamic_backgrounding")&&(window.addEventListener("blur",function(c){b.transition("document_backgrounded",c)}),window.addEventListener("focus",function(c){b.transition("document_foregrounded",c)}))}
r(tr,qr);tr.prototype.i=function(a,b){a(b);L("visibility_lifecycles_dynamic_backgrounding")&&this.transition("document_foregrounded")};
tr.prototype.h=function(a,b){a(b);L("visibility_lifecycles_dynamic_backgrounding")&&this.transition("document_backgrounded")};
tr.prototype.l=function(a,b){a(b)};
tr.prototype.o=function(a,b){a(b)};function ur(){this.h=new sr;this.i=new tr}
ur.prototype.install=function(){var a=Da.apply(0,arguments);this.h.install.apply(this.h,ha(a));this.i.install.apply(this.i,ha(a))};function vr(){ur.call(this);var a={};this.install((a.document_disposed={la:this.j},a));a={};this.install((a.flush_logs={la:this.l},a))}
var wr;r(vr,ur);vr.prototype.l=function(){fk("finalPayload",{csn:Rn()})};
vr.prototype.j=function(){Dn(En)};function xr(){}
xr.getInstance=function(){var a=A("ytglobal.storage_");a||(a=new xr,z("ytglobal.storage_",a,void 0));return a};
xr.prototype.estimate=function(){var a,b,c;return w(function(d){c=navigator;return(null===(a=c.storage)||void 0===a?0:a.estimate)?d.return(c.storage.estimate()):(null===(b=c.webkitTemporaryStorage)||void 0===b?0:b.queryUsageAndQuota)?d.return(yr()):d.return()})};
function yr(){var a=navigator;return new Promise(function(b,c){var d;null!==(d=a.webkitTemporaryStorage)&&void 0!==d&&d.queryUsageAndQuota?a.webkitTemporaryStorage.queryUsageAndQuota(function(e,f){b({usage:e,quota:f})},function(e){c(e)}):c(Error("webkitTemporaryStorage is not supported."))})}
z("ytglobal.storageClass_",xr,void 0);function dk(a,b){var c=this;this.handleError=a;this.h=b;this.i=!1;void 0===self.document||self.addEventListener("beforeunload",function(){c.i=!0});
this.j=Math.random()<=gh("ytidb_transaction_ended_event_rate_limit",.02)}
dk.prototype.logEvent=function(a,b){switch(a){case "IDB_DATA_CORRUPTED":L("idb_data_corrupted_killswitch")||this.h("idbDataCorrupted",b);break;case "IDB_UNEXPECTEDLY_CLOSED":this.h("idbUnexpectedlyClosed",b);break;case "IS_SUPPORTED_COMPLETED":L("idb_is_supported_completed_killswitch")||this.h("idbIsSupportedCompleted",b);break;case "QUOTA_EXCEEDED":zr(this,b);break;case "TRANSACTION_ENDED":this.j&&this.h("idbTransactionEnded",b);break;case "TRANSACTION_UNEXPECTEDLY_ABORTED":a=Object.assign(Object.assign({},
b),{hasWindowUnloaded:this.i}),this.h("idbTransactionAborted",a)}};
function zr(a,b){xr.getInstance().estimate().then(function(c){c=Object.assign(Object.assign({},b),{isSw:void 0===self.document,isIframe:self!==self.top,deviceStorageUsageMbytes:Ar(null===c||void 0===c?void 0:c.usage),deviceStorageQuotaMbytes:Ar(null===c||void 0===c?void 0:c.quota)});a.h("idbQuotaExceeded",c)})}
function Ar(a){return"undefined"===typeof a?"-1":String(Math.ceil(a/1048576))}
;var Br=window;
function Cr(){var a=Br.uaChPolyfill.state;if(0===a.type)fk("clientHintsPolyfillEvent",{clientHintsSupported:!1});else{var b=navigator.userAgent,c=void 0!==a.syntheticUa&&a.syntheticUa===b,d={clientHintsSupported:!0,uaAccessedBeforePolyfill:a.didAccessUaBeforePolyfillAvailable,syntheticUaMatches:c};a.didAccessUaBeforePolyfillAvailable&&(d.uaAccessBeforePolyfillCount=a.uaAccessBeforePolyfillCount,a.firstAccessUaError&&(d.firstUaAccessStack=String(a.firstAccessUaError.stack).replace(/\n/g,""),zn(a.firstAccessUaError)),
d.polyfillAvailabilityDelayMs=a.polyfillAvailabilityDelay);fk("clientHintsPolyfillEvent",d);c||(b={syntheticUa:a.syntheticUa,ua:b},b.brand=a.data.brands.map(function(e){return'"'+e.brand+'"; v="'+e.version+'"'}),b.mobileness=a.data.mobile,a=a.data.values,a.architecture&&(b.platformArchitecture=a.architecture),a.model&&(b.model=a.model),a.platform&&(b.platformBrand=a.platform),a.platformVersion&&(b.platformVersion=a.platformVersion),a.uaFullVersion&&(b.fullVersion=a.uaFullVersion),fk("clientHintsPolyfillDiagnostics",
b))}}
var Dr=!1;function Er(){var a;1===(null===(a=Br.uaChPolyfill)||void 0===a?void 0:a.state.type)?Dr||(Br.uaChPolyfill.onReady=Er,Dr=!0):Br.uaChPolyfill&&Cr()}
;function Fr(a,b,c){I.call(this);var d=this;c=c||B("POST_MESSAGE_ORIGIN",void 0)||window.document.location.protocol+"//"+window.document.location.hostname;this.j=b||null;this.K="*";this.l=c;this.sessionId=null;this.channel="widget";this.L=!!a;this.A=function(e){a:if(!("*"!=d.l&&e.origin!=d.l||d.j&&e.source!=d.j||"string"!==typeof e.data)){try{var f=JSON.parse(e.data)}catch(g){break a}if(!(null==f||d.L&&(d.sessionId&&d.sessionId!=f.id||d.channel&&d.channel!=f.channel))&&f)switch(f.event){case "listening":"null"!=
e.origin&&(d.l=d.K=e.origin);d.j=e.source;d.sessionId=f.id;d.i&&(d.i(),d.i=null);break;case "command":d.m&&(!d.u||0<=bb(d.u,f.func))&&d.m(f.func,f.args,e.origin)}}};
this.u=this.i=this.m=null;window.addEventListener("message",this.A)}
r(Fr,I);Fr.prototype.sendMessage=function(a,b){if(b=b||this.j){this.sessionId&&(a.id=this.sessionId);this.channel&&(a.channel=this.channel);try{var c=JSON.stringify(a);b.postMessage(c,this.K)}catch(d){rh(d)}}};
Fr.prototype.H=function(){window.removeEventListener("message",this.A);I.prototype.H.call(this)};function Gr(){this.i=[];this.isReady=!1;this.j={};var a=this.h=new Fr(!!B("WIDGET_ID_ENFORCE")),b=this.hc.bind(this);a.m=b;a.u=null;this.h.channel="widget";if(a=B("WIDGET_ID"))this.h.sessionId=a}
l=Gr.prototype;l.hc=function(a,b,c){"addEventListener"===a&&b?(a=b[0],this.j[a]||"onReady"===a||(this.addEventListener(a,Hr(this,a)),this.j[a]=!0)):this.lb(a,b,c)};
l.lb=function(){};
function Hr(a,b){return function(c){return a.sendMessage(b,c)}}
l.addEventListener=function(){};
l.Vb=function(){this.isReady=!0;this.sendMessage("initialDelivery",this.Za());this.sendMessage("onReady");cb(this.i,this.Ib,this);this.i=[]};
l.Za=function(){return null};
function Ir(a,b){a.sendMessage("infoDelivery",b)}
l.Ib=function(a){this.isReady?this.h.sendMessage(a):this.i.push(a)};
l.sendMessage=function(a,b){this.Ib({event:a,info:void 0===b?null:b})};
l.dispose=function(){this.h=null};function Jr(a){return(0===a.search("cue")||0===a.search("load"))&&"loadModule"!==a}
function Kr(a,b,c){if("string"===typeof a)return{videoId:a,startSeconds:b,suggestedQuality:c};b=["endSeconds","startSeconds","mediaContentUrl","suggestedQuality","videoId"];c={};for(var d=0;d<b.length;d++){var e=b[d];a[e]&&(c[e]=a[e])}return c}
function Lr(a,b,c,d){if(Ma(a)&&!Array.isArray(a)){b="playlist list listType index startSeconds suggestedQuality".split(" ");c={};for(d=0;d<b.length;d++){var e=b[d];a[e]&&(c[e]=a[e])}return c}b={index:b,startSeconds:c,suggestedQuality:d};"string"===typeof a&&16===a.length?b.list="PL"+a:b.playlist=a;return b}
;function Mr(a){Gr.call(this);this.listeners=[];this.api=a;this.addEventListener("onReady",this.onReady.bind(this));this.addEventListener("onVideoProgress",this.sc.bind(this));this.addEventListener("onVolumeChange",this.tc.bind(this));this.addEventListener("onApiChange",this.lc.bind(this));this.addEventListener("onPlaybackQualityChange",this.oc.bind(this));this.addEventListener("onPlaybackRateChange",this.pc.bind(this));this.addEventListener("onStateChange",this.qc.bind(this));this.addEventListener("onWebglSettingsChanged",
this.uc.bind(this))}
r(Mr,Gr);l=Mr.prototype;
l.lb=function(a,b,c){if(this.api.isExternalMethodAvailable(a,c)){b=b||[];if(0<b.length&&Jr(a)){var d=b;if(Ma(d[0])&&!Array.isArray(d[0]))var e=d[0];else switch(e={},a){case "loadVideoById":case "cueVideoById":e=Kr(d[0],void 0!==d[1]?Number(d[1]):void 0,d[2]);break;case "loadVideoByUrl":case "cueVideoByUrl":e=d[0];"string"===typeof e&&(e={mediaContentUrl:e,startSeconds:void 0!==d[1]?Number(d[1]):void 0,suggestedQuality:d[2]});b:{if((d=e.mediaContentUrl)&&(d=/\/([ve]|embed)\/([^#?]+)/.exec(d))&&d[2]){d=
d[2];break b}d=null}e.videoId=d;e=Kr(e);break;case "loadPlaylist":case "cuePlaylist":e=Lr(d[0],d[1],d[2],d[3])}b.length=1;b[0]=e}this.api.handleExternalCall(a,b,c);Jr(a)&&Ir(this,this.Za())}};
l.onReady=function(){var a=this.Vb.bind(this);this.h.i=a};
l.addEventListener=function(a,b){this.listeners.push({eventType:a,listener:b});this.api.addEventListener(a,b)};
l.Za=function(){if(!this.api)return null;var a=this.api.getApiInterface();ib(a,"getVideoData");for(var b={apiInterface:a},c=0,d=a.length;c<d;c++){var e=a[c];if(0===e.search("get")||0===e.search("is")){var f=0;0===e.search("get")?f=3:0===e.search("is")&&(f=2);f=e.charAt(f).toLowerCase()+e.substr(f+1);try{var g=this.api[e]();b[f]=g}catch(h){}}}b.videoData=this.api.getVideoData();b.currentTimeLastUpdated_=Date.now()/1E3;return b};
l.qc=function(a){a={playerState:a,currentTime:this.api.getCurrentTime(),duration:this.api.getDuration(),videoData:this.api.getVideoData(),videoStartBytes:0,videoBytesTotal:this.api.getVideoBytesTotal(),videoLoadedFraction:this.api.getVideoLoadedFraction(),playbackQuality:this.api.getPlaybackQuality(),availableQualityLevels:this.api.getAvailableQualityLevels(),currentTimeLastUpdated_:Date.now()/1E3,playbackRate:this.api.getPlaybackRate(),mediaReferenceTime:this.api.getMediaReferenceTime()};this.api.getVideoUrl&&
(a.videoUrl=this.api.getVideoUrl());this.api.getVideoContentRect&&(a.videoContentRect=this.api.getVideoContentRect());this.api.getProgressState&&(a.progressState=this.api.getProgressState());this.api.getPlaylist&&(a.playlist=this.api.getPlaylist());this.api.getPlaylistIndex&&(a.playlistIndex=this.api.getPlaylistIndex());this.api.getStoryboardFormat&&(a.storyboardFormat=this.api.getStoryboardFormat());Ir(this,a)};
l.oc=function(a){Ir(this,{playbackQuality:a})};
l.pc=function(a){Ir(this,{playbackRate:a})};
l.lc=function(){for(var a=this.api.getOptions(),b={namespaces:a},c=0,d=a.length;c<d;c++){var e=a[c],f=this.api.getOptions(e);b[e]={options:f};for(var g=0,h=f.length;g<h;g++){var k=f[g],m=this.api.getOption(e,k);b[e][k]=m}}this.sendMessage("apiInfoDelivery",b)};
l.tc=function(){Ir(this,{muted:this.api.isMuted(),volume:this.api.getVolume()})};
l.sc=function(a){a={currentTime:a,videoBytesLoaded:this.api.getVideoBytesLoaded(),videoLoadedFraction:this.api.getVideoLoadedFraction(),currentTimeLastUpdated_:Date.now()/1E3,playbackRate:this.api.getPlaybackRate(),mediaReferenceTime:this.api.getMediaReferenceTime()};this.api.getProgressState&&(a.progressState=this.api.getProgressState());Ir(this,a)};
l.uc=function(){var a={sphericalProperties:this.api.getSphericalProperties()};Ir(this,a)};
l.dispose=function(){Gr.prototype.dispose.call(this);for(var a=0;a<this.listeners.length;a++){var b=this.listeners[a];this.api.removeEventListener(b.eventType,b.listener)}this.listeners=[]};function Nr(a){I.call(this);this.i={};this.started=!1;this.connection=a;this.connection.subscribe("command",this.Eb,this)}
r(Nr,I);l=Nr.prototype;l.start=function(){this.started||this.h()||(this.started=!0,this.connection.na("RECEIVING"))};
l.na=function(a,b){this.started&&!this.h()&&this.connection.na(a,b)};
l.Eb=function(a,b,c){if(this.started&&!this.h()){var d=b||{};switch(a){case "addEventListener":"string"===typeof d.event&&this.addListener(d.event);break;case "removeEventListener":"string"===typeof d.event&&this.removeListener(d.event);break;default:this.api.isReady()&&this.api.isExternalMethodAvailable(a,c||null)&&(b=Or(a,b||{}),c=this.api.handleExternalCall(a,b,c||null),(c=Pr(a,c))&&this.na(a,c))}}};
l.addListener=function(a){if(!(a in this.i)){var b=this.mc.bind(this,a);this.i[a]=b;this.addEventListener(a,b)}};
l.mc=function(a,b){this.started&&!this.h()&&this.connection.na(a,this.Ya(a,b))};
l.Ya=function(a,b){if(null!=b)return{value:b}};
l.removeListener=function(a){a in this.i&&(this.removeEventListener(a,this.i[a]),delete this.i[a])};
l.H=function(){var a=this.connection;a.h()||Lf(a.i,"command",this.Eb,this);this.connection=null;for(var b in this.i)this.i.hasOwnProperty(b)&&this.removeListener(b);I.prototype.H.call(this)};function Qr(a,b){Nr.call(this,b);this.api=a;this.start()}
r(Qr,Nr);Qr.prototype.addEventListener=function(a,b){this.api.addEventListener(a,b)};
Qr.prototype.removeEventListener=function(a,b){this.api.removeEventListener(a,b)};
function Or(a,b){switch(a){case "loadVideoById":return a=Kr(b),[a];case "cueVideoById":return a=Kr(b),[a];case "loadVideoByPlayerVars":return[b];case "cueVideoByPlayerVars":return[b];case "loadPlaylist":return a=Lr(b),[a];case "cuePlaylist":return a=Lr(b),[a];case "seekTo":return[b.seconds,b.allowSeekAhead];case "playVideoAt":return[b.index];case "setVolume":return[b.volume];case "setPlaybackQuality":return[b.suggestedQuality];case "setPlaybackRate":return[b.suggestedRate];case "setLoop":return[b.loopPlaylists];
case "setShuffle":return[b.shufflePlaylist];case "getOptions":return[b.module];case "getOption":return[b.module,b.option];case "setOption":return[b.module,b.option,b.value];case "handleGlobalKeyDown":return[b.keyCode,b.shiftKey,b.ctrlKey,b.altKey,b.metaKey,b.key,b.code]}return[]}
function Pr(a,b){switch(a){case "isMuted":return{muted:b};case "getVolume":return{volume:b};case "getPlaybackRate":return{playbackRate:b};case "getAvailablePlaybackRates":return{availablePlaybackRates:b};case "getVideoLoadedFraction":return{videoLoadedFraction:b};case "getPlayerState":return{playerState:b};case "getCurrentTime":return{currentTime:b};case "getPlaybackQuality":return{playbackQuality:b};case "getAvailableQualityLevels":return{availableQualityLevels:b};case "getDuration":return{duration:b};
case "getVideoUrl":return{videoUrl:b};case "getVideoEmbedCode":return{videoEmbedCode:b};case "getPlaylist":return{playlist:b};case "getPlaylistIndex":return{playlistIndex:b};case "getOptions":return{options:b};case "getOption":return{option:b}}}
Qr.prototype.Ya=function(a,b){switch(a){case "onReady":return;case "onStateChange":return{playerState:b};case "onPlaybackQualityChange":return{playbackQuality:b};case "onPlaybackRateChange":return{playbackRate:b};case "onError":return{errorCode:b}}return Nr.prototype.Ya.call(this,a,b)};
Qr.prototype.H=function(){Nr.prototype.H.call(this);delete this.api};function Rr(a){a=void 0===a?!1:a;I.call(this);this.i=new J(a);Md(this,Ua(Kd,this.i))}
Wa(Rr,I);Rr.prototype.subscribe=function(a,b,c){return this.h()?0:this.i.subscribe(a,b,c)};
Rr.prototype.l=function(a,b){this.h()||this.i.ka.apply(this.i,arguments)};function Sr(a,b,c){Rr.call(this);this.j=a;this.destination=b;this.id=c}
r(Sr,Rr);Sr.prototype.na=function(a,b){this.h()||this.j.na(this.destination,this.id,a,b)};
Sr.prototype.H=function(){this.destination=this.j=null;Rr.prototype.H.call(this)};function Tr(a,b,c){I.call(this);this.destination=a;this.origin=c;this.i=yh(window,"message",this.j.bind(this));this.connection=new Sr(this,a,b);Md(this,Ua(Kd,this.connection))}
r(Tr,I);Tr.prototype.na=function(a,b,c,d){this.h()||a!==this.destination||(a={id:b,command:c},d&&(a.data=d),this.destination.postMessage(mf(a),this.origin))};
Tr.prototype.j=function(a){var b;if(b=!this.h())if(b=a.origin===this.origin)a:{b=this.destination;do{b:{var c=a.source;do{if(c===b){c=!0;break b}if(c===c.parent)break;c=c.parent}while(null!=c);c=!1}if(c){b=!0;break a}b=b.opener}while(null!=b);b=!1}if(b&&(b=a.data,"string"===typeof b)){try{b=JSON.parse(b)}catch(d){return}b.command&&(c=this.connection,c.h()||c.l("command",b.command,b.data,a.origin))}};
Tr.prototype.H=function(){zh(this.i);this.destination=null;I.prototype.H.call(this)};function Ur(a){a=a||{};var b={},c={};this.url=a.url||"";this.args=a.args||rb(b);this.assets=a.assets||{};this.attrs=a.attrs||rb(c);this.fallback=a.fallback||null;this.fallbackMessage=a.fallbackMessage||null;this.html5=!!a.html5;this.disable=a.disable||{};this.loaded=!!a.loaded;this.messages=a.messages||{}}
Ur.prototype.clone=function(){var a=new Ur,b;for(b in this)if(this.hasOwnProperty(b)){var c=this[b];"object"==Ka(c)?a[b]=rb(c):a[b]=c}return a};var Vr=/cssbin\/(?:debug-)?([a-zA-Z0-9_-]+?)(?:-2x|-web|-rtl|-vfl|.css)/;function Wr(a){a=a||"";if(window.spf){var b=a.match(Vr);spf.style.load(a,b?b[1]:"",void 0)}else Xr(a)}
function Xr(a){var b=Yr(a),c=document.getElementById(b),d=c&&Sq(c,"loaded");d||c&&!d||(c=Zr(a,b,function(){Sq(c,"loaded")||(Qq(c),Ci(b),Bh(Ua(Di,b),0))}))}
function Zr(a,b,c){var d=document.createElement("link");d.id=b;d.onload=function(){c&&setTimeout(c,0)};
a=kf(a);Rb(d,a);(document.getElementsByTagName("head")[0]||document.body).appendChild(d);return d}
function Yr(a){var b=jd("A");Ab("This URL is never added to the DOM");Qb(b,new Ib(a,Jb));a=b.href.replace(/^[a-zA-Z]+:\/\//,"//");return"css-"+Vb(a)}
;function $r(){I.call(this);this.i=[]}
r($r,I);$r.prototype.H=function(){for(;this.i.length;){var a=this.i.pop();a.target.removeEventListener(a.name,a.la,void 0)}I.prototype.H.call(this)};function as(){$r.apply(this,arguments)}
r(as,$r);function bs(a,b,c,d){I.call(this);var e=this;this.L=b;this.webPlayerContextConfig=d;this.Va=!1;this.api={};this.Fa=this.u=null;this.S=new J;this.i={};this.ga=this.Ga=this.elementId=this.Wa=this.config=null;this.Y=!1;this.l=this.A=null;this.Ha={};this.Mb=["onReady"];this.lastError=null;this.mb=NaN;this.K={};this.Nb=new as(this);this.qa=0;this.j=this.m=a;Md(this,Ua(Kd,this.S));cs(this);ds(this);Md(this,Ua(Kd,this.Nb));c?this.qa=Bh(function(){e.loadNewVideoConfig(c)},0):d&&(es(this),fs(this))}
r(bs,I);l=bs.prototype;l.getId=function(){return this.L};
l.loadNewVideoConfig=function(a){if(!this.h()){this.qa&&(Ch(this.qa),this.qa=0);var b=a||{};b instanceof Ur||(b=new Ur(b));this.config=b;this.setConfig(a);fs(this);this.isReady()&&gs(this)}};
function es(a){var b,c;a.webPlayerContextConfig?c=a.webPlayerContextConfig.rootElementId:c=a.config.attrs.id;a.elementId=c||a.elementId;"video-player"===a.elementId&&(a.elementId=a.L,a.webPlayerContextConfig?a.webPlayerContextConfig.rootElementId=a.L:a.config.attrs.id=a.L);(null===(b=a.j)||void 0===b?void 0:b.id)===a.elementId&&(a.elementId+="-player",a.webPlayerContextConfig?a.webPlayerContextConfig.rootElementId=a.elementId:a.config.attrs.id=a.elementId)}
l.setConfig=function(a){var b;this.Wa=a;this.config=hs(a);es(this);this.Ga||(this.Ga=is(this,(null===(b=this.config.args)||void 0===b?void 0:b.jsapicallback)||"onYouTubePlayerReady"));this.config.args?this.config.args.jsapicallback=null:this.config.args={jsapicallback:null};var c;if(null===(c=this.config)||void 0===c?0:c.attrs)a=this.config.attrs,(c=a.width)&&this.j&&(this.j.style.width=ud(Number(c)||c)),(a=a.height)&&this.j&&(this.j.style.height=ud(Number(a)||a))};
function gs(a){var b;a.config&&!0!==a.config.loaded&&(a.config.loaded=!0,!a.config.args||"0"!==a.config.args.autoplay&&0!==a.config.args.autoplay&&!1!==a.config.args.autoplay?a.api.loadVideoByPlayerVars(null!==(b=a.config.args)&&void 0!==b?b:null):a.api.cueVideoByPlayerVars(a.config.args))}
function js(a){var b=!0,c=ks(a);c&&a.config&&(a=ls(a),b=Sq(c,"version")===a);return b&&!!A("yt.player.Application.create")}
function fs(a){if(!a.h()&&!a.Y){var b=js(a);if(b&&"html5"===(ks(a)?"html5":null))a.ga="html5",a.isReady()||ms(a);else if(ns(a),a.ga="html5",b&&a.l&&a.m)a.m.appendChild(a.l),ms(a);else{a.config&&(a.config.loaded=!0);var c=!1;a.A=function(){c=!0;var d=os(a,"player_bootstrap_method")?A("yt.player.Application.createAlternate")||A("yt.player.Application.create"):A("yt.player.Application.create");var e=a.config?hs(a.config):void 0;d&&d(a.m,e,a.webPlayerContextConfig);ms(a)};
a.Y=!0;b?a.A():(Wq(ls(a),a.A),(b=ps(a))&&Wr(b),qs(a)&&!c&&z("yt.player.Application.create",null,void 0))}}}
function ks(a){var b=id(a.elementId);!b&&a.j&&a.j.querySelector&&(b=a.j.querySelector("#"+a.elementId));return b}
function ms(a){var b;if(!a.h()){var c=ks(a),d=!1;c&&c.getApiInterface&&c.getApiInterface()&&(d=!0);d?(a.Y=!1,!os(a,"html5_remove_not_servable_check_killswitch")&&(null===c||void 0===c?0:c.isNotServable)&&a.config&&(null===c||void 0===c?0:c.isNotServable(null===(b=a.config.args)||void 0===b?void 0:b.video_id))||rs(a)):a.mb=Bh(function(){ms(a)},50)}}
function rs(a){cs(a);a.Va=!0;var b=ks(a);if(b){a.u=ss(a,b,"addEventListener");a.Fa=ss(a,b,"removeEventListener");var c=b.getApiInterface();c=c.concat(b.getInternalApiInterface());for(var d=a.api,e=0;e<c.length;e++){var f=c[e];d[f]||(d[f]=ss(a,b,f))}}for(var g in a.i)a.i.hasOwnProperty(g)&&a.u&&a.u(g,a.i[g]);gs(a);a.Ga&&a.Ga(a.api);a.S.ka("onReady",a.api)}
function ss(a,b,c){var d=b[c];return function(){var e=Da.apply(0,arguments);try{return a.lastError=null,d.apply(b,e)}catch(f){"sendAbandonmentPing"!==c&&(f.params=c,a.lastError=f,An(f))}}}
function cs(a){a.Va=!1;if(a.Fa)for(var b in a.i)a.i.hasOwnProperty(b)&&a.Fa(b,a.i[b]);for(var c in a.K)a.K.hasOwnProperty(c)&&Ch(Number(c));a.K={};a.u=null;a.Fa=null;b=a.api;for(var d in b)b.hasOwnProperty(d)&&(b[d]=null);b.addEventListener=function(e,f){a.addEventListener(e,f)};
b.removeEventListener=function(e,f){a.removeEventListener(e,f)};
b.destroy=function(){a.dispose()};
b.getLastError=function(){return a.getLastError()};
b.getPlayerType=function(){return a.getPlayerType()};
b.getCurrentVideoConfig=function(){return a.Wa};
b.loadNewVideoConfig=function(e){a.loadNewVideoConfig(e)};
b.isReady=function(){return a.isReady()}}
l.isReady=function(){return this.Va};
function ds(a){a.addEventListener("WATCH_LATER_VIDEO_ADDED",function(b){Ci("WATCH_LATER_VIDEO_ADDED",b)});
a.addEventListener("WATCH_LATER_VIDEO_REMOVED",function(b){Ci("WATCH_LATER_VIDEO_REMOVED",b)});
a.addEventListener("onAdAnnounce",function(b){Ci("a11y-announce",b)})}
l.addEventListener=function(a,b){var c=this,d=is(this,b);d&&(0<=bb(this.Mb,a)||this.i[a]||(b=ts(this,a),this.u&&this.u(a,b)),this.S.subscribe(a,d),"onReady"===a&&this.isReady()&&Bh(function(){d(c.api)},0))};
l.removeEventListener=function(a,b){this.h()||(b=is(this,b))&&Lf(this.S,a,b)};
function is(a,b){var c=b;if("string"===typeof b){if(a.Ha[b])return a.Ha[b];c=function(){var d=Da.apply(0,arguments),e=A(b);if(e)try{e.apply(y,d)}catch(f){zn(f)}};
a.Ha[b]=c}return c?c:null}
function ts(a,b){var c="ytPlayer"+b+a.L;a.i[b]=c;y[c]=function(d){var e=Bh(function(){if(!a.h()){a.S.ka(b,null!==d&&void 0!==d?d:void 0);var f=a.K,g=String(e);g in f&&delete f[g]}},0);
ob(a.K,String(e))};
return c}
l.getPlayerType=function(){return this.ga||(ks(this)?"html5":null)};
l.getLastError=function(){return this.lastError};
function ns(a){a.cancel();cs(a);a.ga=null;a.config&&(a.config.loaded=!1);var b=ks(a);b&&(js(a)||!qs(a)?a.l=b:(b&&b.destroy&&b.destroy(),a.l=null));if(a.m)for(a=a.m;b=a.firstChild;)a.removeChild(b)}
l.cancel=function(){this.A&&br(ls(this),this.A);Ch(this.mb);this.Y=!1};
l.H=function(){ns(this);if(this.l&&this.config&&this.l.destroy)try{this.l.destroy()}catch(b){zn(b)}this.Ha=null;for(var a in this.i)this.i.hasOwnProperty(a)&&(y[this.i[a]]=null);this.Wa=this.config=this.api=null;delete this.m;delete this.j;I.prototype.H.call(this)};
function qs(a){var b,c;a=null===(c=null===(b=a.config)||void 0===b?void 0:b.args)||void 0===c?void 0:c.fflags;return!!a&&-1!==a.indexOf("player_destroy_old_version=true")}
function ls(a){return a.webPlayerContextConfig?a.webPlayerContextConfig.jsUrl:(a=a.config.assets)?a.js:""}
function ps(a){return a.webPlayerContextConfig?a.webPlayerContextConfig.cssUrl:(a=a.config.assets)?a.css:""}
function os(a,b){var c;if(a.webPlayerContextConfig)var d=a.webPlayerContextConfig.serializedExperimentFlags;else if(null===(c=a.config)||void 0===c?0:c.args)d=a.config.args.fflags;return"true"===Wh(d||"","&")[b]}
function hs(a){for(var b={},c=q(Object.keys(a)),d=c.next();!d.done;d=c.next()){d=d.value;var e=a[d];b[d]="object"===typeof e?rb(e):e}return b}
;var us={},vs="player_uid_"+(1E9*Math.random()>>>0);function ws(a,b,c){var d="player";c=void 0===c?!0:c;d="string"===typeof d?id(d):d;var e=vs+"_"+Na(d),f=us[e];if(f&&c)return xs(a,b)?f.api.loadVideoByPlayerVars(a.args||null):f.loadNewVideoConfig(a),f.api;f=new bs(d,e,a,b);us[e]=f;Ci("player-added",f.api);Md(f,function(){delete us[f.getId()]});
return f.api}
function xs(a,b){return b&&b.serializedExperimentFlags?b.serializedExperimentFlags.includes("web_player_remove_playerproxy=true"):a&&a.args&&a.args.fflags?a.args.fflags.includes("web_player_remove_playerproxy=true"):!1}
;var ys=null,zs=null,As=null;function Bs(){var a=ys.getVideoData(1);a=a.title?a.title+" - YouTube":"YouTube";document.title!==a&&(document.title=a)}
;function Cs(a,b,c){a="ST-"+Vb(a).toString(36);b=b?ac(b):"";c=c||5;fo()&&rj(a,b,c)}
;function Ds(a,b,c){b=void 0===b?{}:b;c=void 0===c?!1:c;var d=B("EVENT_ID");d&&(b.ei||(b.ei=d));if(b){d=a;var e=void 0===e?!0:e;var f=B("VALID_SESSION_TEMPDATA_DOMAINS",[]),g=Yb(window.location.href);g&&f.push(g);g=Yb(d);if(0<=bb(f,g)||!g&&0==d.lastIndexOf("/",0))if(L("autoescape_tempdata_url")&&(f=document.createElement("a"),Qb(f,d),d=f.href),d&&(d=Zb(d),f=d.indexOf("#"),d=0>f?d:d.substr(0,f)))if(e&&!b.csn&&(b.itct||b.ved)&&(b=Object.assign({csn:Rn()},b)),h){var h=parseInt(h,10);isFinite(h)&&0<h&&
Cs(d,b,h)}else Cs(d,b)}if(c)return!1;if((window.ytspf||{}).enabled)spf.navigate(a);else{var k=void 0===k?{}:k;var m=void 0===m?"":m;var n=void 0===n?window:n;c=n.location;a=bc(a,k)+m;var t=void 0===t?td:t;a:{t=void 0===t?td:t;for(k=0;k<t.length;++k)if(m=t[k],m instanceof rd&&m.isValid(a)){t=new cd(a,ad);break a}t=void 0}c.href=ed(t||dd)}return!0}
;z("yt.setConfig",dh,void 0);z("yt.config.set",dh,void 0);z("yt.setMsg",Wn,void 0);z("yt.msgs.set",Wn,void 0);z("yt.logging.errors.log",zn,void 0);
z("writeEmbed",function(){var a=B("PLAYER_CONFIG",void 0);if(!a){var b=B("PLAYER_VARS",void 0);b&&(a={args:b})}no(!0);"gvn"===a.args.ps&&(document.body.style.backgroundColor="transparent");a.attrs||(a.attrs={width:"100%",height:"100%",id:"video-player"});var c=document.referrer;b=B("POST_MESSAGE_ORIGIN");window!==window.top&&c&&c!==document.URL&&(a.args.loaderUrl=c);L("embeds_js_api_set_1p_cookie")&&(c=ai(window.location.href),c.embedsTokenValue&&(a.args.embedsTokenValue=c.embedsTokenValue));Rp();
if((c=B("WEB_PLAYER_CONTEXT_CONFIGS",void 0))&&"WEB_PLAYER_CONTEXT_CONFIG_ID_EMBEDDED_PLAYER"in c){c=c.WEB_PLAYER_CONTEXT_CONFIG_ID_EMBEDDED_PLAYER;if(!c.serializedForcedExperimentIds){var d=ai(window.location.href);d.forced_experiments&&(c.serializedForcedExperimentIds=d.forced_experiments)}ys=ws(a,c,!1)}else ys=ws(a);ys.addEventListener("onVideoDataChange",Bs);a=B("POST_MESSAGE_ID","player");B("ENABLE_JS_API")?As=new Mr(ys):B("ENABLE_POST_API")&&"string"===typeof a&&"string"===typeof b&&(zs=new Tr(window.parent,
a,b),As=new Qr(ys,zs.connection));er();L("ytidb_create_logger_embed_killswitch")||ck();L("flush_gel_on_teardown")&&(a={},wr||(wr=new vr),wr.install((a.flush_logs={la:function(){Vi()}},a)));
L("networkless_logging_web_embedded")&&(L("embeds_web_enable_new_nwl")?Km():Sm());L("embeds_enable_ua_ch_polyfill")&&Er();L("ytidb_clear_embedded_player")&&Lh.M(function(){if(!tq){var e={pb:{feedbackEndpoint:Co(oq),modifyChannelNotificationPreferenceEndpoint:Co(pq),playlistEditEndpoint:Co(qq),subscribeEndpoint:Co(mq),unsubscribeEndpoint:Co(nq),webPlayerShareEntityServiceEndpoint:Co(rq)}},f=L("web_enable_client_location_service")?yo.getInstance():void 0,g=[],h={};f&&(g.push(f),h.client_location=f);
if(void 0===k){oo.h||(oo.h=new oo);var k=oo.h}if(void 0===m){sq.h||(sq.h=new sq);var m=sq.h}aq(e,m,k,g,h);tq=$p.h}Pq()})},void 0);
var Es=ph(function(){Xp();var a=vj.getInstance(),b=yj(119),c=1<window.devicePixelRatio;if(document.body&&Ue(document.body,"exp-invert-logo"))if(c&&!Ue(document.body,"inverted-hdpi")){var d=document.body;if(d.classList)d.classList.add("inverted-hdpi");else if(!Ue(d,"inverted-hdpi")){var e=Se(d);Te(d,e+(0<e.length?" inverted-hdpi":"inverted-hdpi"))}}else!c&&Ue(document.body,"inverted-hdpi")&&Ve();if(b!=c){b="f"+(Math.floor(119/31)+1);d=zj(b)||0;d=c?d|67108864:d&-67108865;0==d?delete uj[b]:(c=d.toString(16),
uj[b]=c.toString());c=!0;L("web_secure_pref_cookie_killswitch")&&(c=!1);b=a.h;d=[];for(var f in uj)d.push(f+"="+encodeURIComponent(String(uj[f])));rj(b,d.join("&"),63072E3,a.i,c)}Dq.h||(Dq.h=new Dq);a=Dq.h;f=16623;var g=void 0===g?{}:g;Object.values(Xn).includes(f)||(An(new ik("createClientScreen() called with a non-page VE",f)),f=83769);g.isHistoryNavigation||a.h.push({rootVe:f,key:g.key||""});a.o=[];a.D=[];g.sb?Gq(a,f,g):Hq(a,f,g)}),Fs=ph(function(){ys&&ys.sendAbandonmentPing&&ys.sendAbandonmentPing();
B("PL_ATT")&&kr.dispose();for(var a=0,b=cr.length;a<b;a++)Lh.U(cr[a]);cr.length=0;ar("//static.doubleclick.net/instream/ad_status.js");dr=!1;dh("DCLKSTAT",0);Ld(As,zs);ys&&(ys.removeEventListener("onVideoDataChange",Bs),ys.destroy())});
window.addEventListener?(window.addEventListener("load",Es),window.addEventListener("unload",Fs)):window.attachEvent&&(window.attachEvent("onload",Es),window.attachEvent("onunload",Fs));Va("yt.abuse.player.botguardInitialized",A("yt.abuse.player.botguardInitialized")||lr);Va("yt.abuse.player.invokeBotguard",A("yt.abuse.player.invokeBotguard")||mr);Va("yt.abuse.dclkstatus.checkDclkStatus",A("yt.abuse.dclkstatus.checkDclkStatus")||fr);
Va("yt.player.exports.navigate",A("yt.player.exports.navigate")||Ds);Va("yt.util.activity.init",A("yt.util.activity.init")||Nh);Va("yt.util.activity.getTimeSinceActive",A("yt.util.activity.getTimeSinceActive")||Qh);Va("yt.util.activity.setTimestamp",A("yt.util.activity.setTimestamp")||Oh);}).call(this);
