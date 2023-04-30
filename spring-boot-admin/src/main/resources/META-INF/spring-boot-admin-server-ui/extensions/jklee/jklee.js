(function(global, factory) {
  typeof exports === "object" && typeof module !== "undefined" ? factory(require("vue")) : typeof define === "function" && define.amd ? define(["vue"], factory) : (global = typeof globalThis !== "undefined" ? globalThis : global || self, factory(global.Vue));
})(this, function(vue) {
  "use strict";var __async = (__this, __arguments, generator) => {
  return new Promise((resolve, reject) => {
    var fulfilled = (value) => {
      try {
        step(generator.next(value));
      } catch (e) {
        reject(e);
      }
    };
    var rejected = (value) => {
      try {
        step(generator.throw(value));
      } catch (e) {
        reject(e);
      }
    };
    var step = (x) => x.done ? resolve(x.value) : Promise.resolve(x.value).then(fulfilled, rejected);
    step((generator = generator.apply(__this, __arguments)).next());
  });
};

  const jklee_vue_vue_type_style_index_0_lang = "";
  const _export_sfc = (sfc, props) => {
    const target = sfc.__vccOpts || sfc;
    for (const [key, val] of props) {
      target[key] = val;
    }
    return target;
  };
  const _sfc_main = {
    props: {
      instance: {
        type: Object,
        required: true
      }
    },
    data: () => ({
      settings: "d",
      results: [],
      profileRawArguments: "start,event=itimer,interval=1ms",
      profileSessionName: "test",
      profileDuration: "2s",
      profileFormat: "FLAMEGRAPH",
      profiling: false
    }),
    methods: {
      profile(sessionName, rawArguments, profileDuration, profileFormat) {
        return __async(this, null, function* () {
          this.profiling = true;
          const durationInMillis = parseToMillis(profileDuration);
          setTimeout(() => {
            this.profiling = false;
            this.updateResultsList();
          }, durationInMillis);
          yield this.instance.axios.post(
            `actuator/jklee-profile/${sessionName}`,
            {
              rawArguments,
              duration: profileDuration,
              format: profileFormat
            }
          ).then((response) => {
            console.log(response);
          });
        });
      },
      updateResultsList() {
        return __async(this, null, function* () {
          const response = yield this.instance.axios.get("actuator/jklee-files");
          this.results = response.data.results;
        });
      }
    },
    created() {
      return __async(this, null, function* () {
        const response = yield this.instance.axios.get("actuator/jklee-settings");
        this.settings = response.data;
        yield this.updateResultsList();
      });
    }
  };
  function parseToMillis(timeString) {
    let value = parseInt(timeString, 10);
    let unit = timeString.slice(-1);
    let millis = 0;
    if (unit === "s") {
      millis = value * 1e3;
    } else if (unit === "m") {
      millis = value * 60 * 1e3;
    } else if (unit === "h") {
      millis = value * 60 * 60 * 1e3;
    } else if (unit === "d") {
      millis = value * 60 * 60 * 24 * 1e3;
    }
    return millis;
  }
  const _hoisted_1 = { class: "jklee" };
  const _hoisted_2 = /* @__PURE__ */ vue.createElementVNode("br", null, null, -1);
  const _hoisted_3 = ["textContent"];
  const _hoisted_4 = { class: "content info" };
  const _hoisted_5 = { class: "table" };
  const _hoisted_6 = { class: "info__key" };
  const _hoisted_7 = ["href"];
  const _hoisted_8 = /* @__PURE__ */ vue.createElementVNode("i", { class: "fa fa-download" }, null, -1);
  const _hoisted_9 = ["textContent"];
  const _hoisted_10 = ["textContent"];
  const _hoisted_11 = /* @__PURE__ */ vue.createElementVNode("br", null, null, -1);
  const _hoisted_12 = /* @__PURE__ */ vue.createElementVNode("br", null, null, -1);
  const _hoisted_13 = /* @__PURE__ */ vue.createElementVNode("br", null, null, -1);
  const _hoisted_14 = /* @__PURE__ */ vue.createElementVNode("br", null, null, -1);
  const _hoisted_15 = /* @__PURE__ */ vue.createElementVNode("span", null, "Start profiling", -1);
  const _hoisted_16 = {
    key: 1,
    class: "loader"
  };
  function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_sba_panel = vue.resolveComponent("sba-panel");
    const _component_font_awesome_icon = vue.resolveComponent("font-awesome-icon");
    const _component_sba_button = vue.resolveComponent("sba-button");
    return vue.openBlock(), vue.createElementBlock("div", _hoisted_1, [
      vue.createElementVNode("pre", null, [
        vue.createTextVNode("Raw settings:"),
        _hoisted_2,
        vue.createElementVNode("span", {
          textContent: vue.toDisplayString(_ctx.settings)
        }, null, 8, _hoisted_3)
      ]),
      vue.createVNode(_component_sba_panel, { title: "Profiling results" }, {
        default: vue.withCtx(() => [
          vue.createElementVNode("div", _hoisted_4, [
            vue.createElementVNode("table", _hoisted_5, [
              (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.results, (result) => {
                return vue.openBlock(), vue.createElementBlock("tr", {
                  key: result.name
                }, [
                  vue.createElementVNode("td", _hoisted_6, [
                    vue.createElementVNode("a", {
                      class: "btn btn-primary",
                      href: `instances/${$props.instance.id}/actuator/jklee-files/${result.name}`
                    }, [
                      _hoisted_8,
                      vue.createTextVNode("  "),
                      vue.createElementVNode("span", {
                        textContent: vue.toDisplayString(result.name)
                      }, null, 8, _hoisted_9)
                    ], 8, _hoisted_7)
                  ]),
                  vue.createElementVNode("td", {
                    class: "info__key",
                    textContent: vue.toDisplayString(result.endedAt)
                  }, null, 8, _hoisted_10)
                ]);
              }), 128))
            ])
          ])
        ]),
        _: 1
      }),
      vue.createElementVNode("div", null, [
        vue.createTextVNode(" sessionName: "),
        vue.withDirectives(vue.createElementVNode("input", {
          type: "text",
          "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => _ctx.profileSessionName = $event)
        }, null, 512), [
          [vue.vModelText, _ctx.profileSessionName]
        ]),
        _hoisted_11,
        vue.createTextVNode(" rawArguments: "),
        vue.withDirectives(vue.createElementVNode("input", {
          type: "text",
          "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => _ctx.profileRawArguments = $event)
        }, null, 512), [
          [vue.vModelText, _ctx.profileRawArguments]
        ]),
        _hoisted_12,
        vue.createTextVNode(" profileFormat: "),
        vue.withDirectives(vue.createElementVNode("input", {
          type: "text",
          "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => _ctx.profileFormat = $event)
        }, null, 512), [
          [vue.vModelText, _ctx.profileFormat]
        ]),
        _hoisted_13,
        vue.createTextVNode(" profileDuration: "),
        vue.withDirectives(vue.createElementVNode("input", {
          type: "text",
          "onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => _ctx.profileDuration = $event)
        }, null, 512), [
          [vue.vModelText, _ctx.profileDuration]
        ]),
        _hoisted_14,
        vue.createVNode(_component_sba_button, {
          disabled: _ctx.profiling,
          onClick: _cache[4] || (_cache[4] = ($event) => $options.profile(_ctx.profileSessionName, _ctx.profileRawArguments, _ctx.profileDuration, _ctx.profileFormat))
        }, {
          default: vue.withCtx(() => [
            !_ctx.profiling ? (vue.openBlock(), vue.createElementBlock(vue.Fragment, { key: 0 }, [
              vue.createVNode(_component_font_awesome_icon, { icon: "download" }),
              vue.createTextVNode("  "),
              _hoisted_15
            ], 64)) : (vue.openBlock(), vue.createElementBlock("div", _hoisted_16, "Profiling..."))
          ]),
          _: 1
        }, 8, ["disabled"])
      ])
    ]);
  }
  const jkleeEndpoint = /* @__PURE__ */ _export_sfc(_sfc_main, [["render", _sfc_render]]);
  SBA.viewRegistry.addView({
    name: "instances/jklee",
    parent: "instances",
    path: "jklee",
    component: jkleeEndpoint,
    label: "jklee",
    isEnabled: ({ instance }) => {
      return instance.hasEndpoint("jklee-settings");
    }
  });
});
//# sourceMappingURL=jklee.js.map
