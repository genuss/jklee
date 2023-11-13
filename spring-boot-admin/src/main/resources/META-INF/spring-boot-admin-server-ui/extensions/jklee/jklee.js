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
      error: null,
      hasLoaded: false,
      settings: [],
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
            `actuator/jkleeProfile/${sessionName}`,
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
          const response = yield this.instance.axios.get("actuator/jkleeFiles");
          this.results = response.data.results;
        });
      }
    },
    created() {
      return __async(this, null, function* () {
        try {
          const response = yield this.instance.axios.get("actuator/jkleeSettings");
          this.settings = response.data.settings;
        } catch (error) {
          this.error = error;
        } finally {
          this.hasLoaded = true;
        }
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
  const _hoisted_1 = { class: "flex-1 sm:break-all" };
  const _hoisted_2 = ["textContent"];
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
    const _component_sba_instance_section = vue.resolveComponent("sba-instance-section");
    return vue.openBlock(), vue.createBlock(_component_sba_instance_section, {
      error: _ctx.error,
      loading: !_ctx.hasLoaded
    }, {
      default: vue.withCtx(() => [
        vue.createVNode(_component_sba_panel, { title: "Settings" }, {
          default: vue.withCtx(() => [
            (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.settings, (setting, index) => {
              return vue.openBlock(), vue.createElementBlock("div", {
                key: _ctx.settings.name
              }, [
                vue.createElementVNode("div", {
                  class: vue.normalizeClass(["flex items-center px-4 py-3", {
                    "bg-gray-50": index % 2 === 0
                  }])
                }, [
                  vue.createElementVNode("div", _hoisted_1, [
                    vue.createElementVNode("span", {
                      textContent: vue.toDisplayString(setting.name)
                    }, null, 8, _hoisted_2)
                  ]),
                  vue.createElementVNode("div", null, [
                    vue.createElementVNode("span", {
                      textContent: vue.toDisplayString(setting.value)
                    }, null, 8, _hoisted_3)
                  ])
                ], 2)
              ]);
            }), 128))
          ]),
          _: 1
        }),
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
                        href: `instances/${$props.instance.id}/actuator/jkleeFiles/${result.name}`
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
      ]),
      _: 1
    }, 8, ["error", "loading"]);
  }
  const jkleeEndpoint = /* @__PURE__ */ _export_sfc(_sfc_main, [["render", _sfc_render]]);
  SBA.viewRegistry.addView({
    group: "jklee",
    name: "instances/jklee",
    parent: "instances",
    path: "jklee",
    component: jkleeEndpoint,
    label: "jklee",
    isEnabled: ({ instance }) => {
      return instance.hasEndpoint("jkleeSettings");
    }
  });
  SBA.viewRegistry.setGroupIcon(
    "jklee",
    `<svg xmlns="http://www.w3.org/2000/svg"
          class='h-5 w-5 mr-3'
          viewBox="0 0 512 512"
          xml:space="preserve">
<g>
    <g>
        <path d="M477.109,245.333c15.189-14.432,24.224-33.899,24.224-53.333c0-42.731-51.413-74.667-97.387-74.667
            c-4.181,0-13.077,0-31.371,10.347c6.912-12.597,10.357-23.147,10.357-31.68c0-45.408-30.432-96-74.08-96
            C288.93,0.005,269.86,8.087,256,22.4C242.143,8.09,223.077,0.008,203.157,0c-43.648,0-74.091,50.581-74.091,96
            c0,8.533,3.413,19.093,10.336,31.68c-18.283-10.347-27.179-10.347-31.349-10.347c-45.973,0-97.387,31.925-97.387,74.667
            c0,19.435,9.045,38.901,24.235,53.333c-15.189,14.443-24.235,33.909-24.235,53.333c0,42.741,51.413,74.667,97.387,74.667
            c34.667,0,105.675-33.387,137.28-49.163v109.483c0,44.672,18.997,78.347,44.192,78.347H320c5.891,0,10.667-4.776,10.667-10.667
            s-4.776-10.667-10.667-10.667h-30.475c-9.099,0-22.859-22.741-22.859-57.013V324.171c31.605,15.776,102.613,49.163,137.28,49.163
            c45.973,0,97.387-31.936,97.387-74.667C501.333,279.243,492.288,259.776,477.109,245.333z M403.947,352
            c-32.48,0-114.208-39.467-142.933-54.677c-3.123-1.654-6.861-1.654-9.984,0C222.261,312.533,140.533,352,108.053,352
            C68.96,352,32,326.069,32,298.667c0-20,13.355-36.085,25.856-44.48c4.893-3.281,6.199-9.908,2.917-14.8
            c-0.773-1.153-1.764-2.144-2.917-2.917C45.355,228.096,32,212.011,32,192c0-27.403,36.96-53.333,76.053-53.333
            c6.699,0,28.16,5.44,82.581,55.819c4.343,3.98,11.091,3.685,15.071-0.658c3.707-4.046,3.74-10.244,0.076-14.329
            C155.819,124.469,150.4,102.773,150.4,96c0-38.4,25.6-74.667,52.725-74.667c17.728,0.019,34.249,8.986,43.925,23.84
            c3.237,4.922,9.851,6.288,14.773,3.051c1.214-0.799,2.252-1.837,3.051-3.051c9.69-14.857,26.22-23.822,43.957-23.84
            C336,21.333,361.6,57.6,361.6,96c0,6.773-5.397,28.459-55.349,83.509c-3.969,4.353-3.658,11.1,0.695,15.069
            c4.102,3.74,10.387,3.709,14.452-0.072c54.4-50.4,75.883-55.829,82.571-55.829c39.093,0,76.053,25.92,76.053,53.333
            c0,20.032-13.344,36.107-25.845,44.469c-4.895,3.277-6.207,9.902-2.93,14.798c0.776,1.159,1.771,2.155,2.93,2.93
            C466.645,262.56,480,278.645,480,298.667C480,326.08,443.04,352,403.947,352z"/>
    </g>
</g>
</svg>`
  );
});
//# sourceMappingURL=jklee.js.map
