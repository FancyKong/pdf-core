/*! embedSWF 0.1.0
 * (c) 2013 Jony Zhang <zj86@live.cn>, MIT Licensed
 * https://github.com/niceue/embedSWF/
 */
(function (e) {
    "function" == typeof define ? define(function (t, n, a) {
        return e(a.uri)
    }) : e()
})(function (e) {
    function t(e, i) {
        var r, g, b, y, w = "", m = {type: "application/x-shockwave-flash", width: "100%", height: "100%"},
            v = {wmode: "transparent", menu: !1, allowScriptAccess: "always"};
        if (!i && e && (i = e, e = null), "[object Object]" === Object.prototype.toString.call(i) && i.src) {
            if (e && (g = l.getElementById(e)), function (e) {
                    for (var t, n = e.length, a = {}; n--;)a[e[n]] = 1;
                    for (t in i)a[t] ? m[t] = i[t] : v[t] = i[t]
                }(s.split(" ")), u) {
                if (w = a(m, v), parseFloat(i.version || h) > u) {
                    if (d)return;
                    y = 2, p = w, m.id = f, b = i.base || t.base || "", b && "/" !== b.slice(-1) && (b += "/"), v.src = b + c, !/%$/.test(m.width) && 310 > parseInt(m.width, 10) && (m.width = "310"), !/%$/.test(m.height) && 137 > parseInt(m.height, 10) && (m.height = "137"), r = "MMredirectURL=" + location.href + "&MMplayerType=" + (o ? "ActiveX" : "PlugIn") + "&MMdoctitle=" + encodeURIComponent(l.title.slice(0, 47) + " - Flash Player Installation"), v.flashvars = v.flashvars ? v.flashvars + "&" + r : r, w = a(m, v), d = !0
                }
            } else g && g.firstChild || (w = n(m)), y = 1;
            return g && w && (g.innerHTML = w), y && "function" == typeof i.fallback && i.fallback(y), w
        }
    }

    function n(e) {
        var t = e.width, n = e.height, a = "number" == typeof t ? t + "px" : t, i = "number" == typeof n ? n + "px" : n;
        return '<a target="_blank" href="//www.adobe.com/go/getflash"><span style="display:block;cursor:pointer;background:#EFEFEF url(//www.adobe.com/images/shared/download_buttons/get_flash_player.gif) center center no-repeat;width:' + a + ";height:" + i + ';" title="Get Adobe Flash player">' + "</span></a>"
    }

    function a(e, t) {
        var n, a = "";
        if (o) {
            e.classid = "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000", a += "<object" + i(e) + ">";
            for (n in t)a += '<param name="' + n + '" value="' + t[n] + '">';
            a += "</object>"
        } else a += "<embed" + i(t) + i(e) + ">";
        return a
    }

    function i(e) {
        var t, n = "";
        for (t in e)n += " " + t + '="' + e[t] + '"';
        return n
    }

    function r(e) {
        "string" == typeof e && (e = l.getElementById(e).firstChild), e && e.tagName && (o && "OBJECT" === e.tagName ? (e.style.display = "none", function () {
            if (4 == e.readyState) {
                for (var t in e)"function" == typeof e[t] && (e[t] = null);
                e.parentNode.removeChild(e)
            } else setTimeout(arguments.callee, 15)
        }()) : e.parentNode.removeChild(e), d = !1)
    }

    var l = document, o = !!window.ActiveXObject,
        s = "width height name id class style title type align tabindex usemap", c = "expressInstall.swf?" + +new Date,
        f = "ExpressInstall", d = !1, p = "", h = "9,0,28,0", u = function () {
            var e, t, n = "ShockwaveFlash";
            if (o)try {
                e = new ActiveXObject(n + "." + n).GetVariable("$version"), e && (e = e.split(" ")[1].split(",").join("."))
            } catch (a) {
            } else t = navigator.plugins["Shockwave Flash"], "object" == typeof t && (e = t.description.split(" ")[2]);
            return parseFloat(e)
        }();
    return t.destroy = r, t.flashVersion = u, t.base = function (e) {
        if (!e) {
            var t = l.getElementsByTagName("script"), n = t[t.length - 1];
            e = n.src
        }
        return e.split("/").slice(0, -1).join("/")
    }(e), t.installCallback = function () {
        if (d) {
            var e = l.getElementById(f), t = e.parentNode;
            r(e), t.innerHTML = p, p = "", d = !1
        }
    }, window.embedSWF = t
});
