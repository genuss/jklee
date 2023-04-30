import jkleeEndpoint from './jklee.vue';

SBA.viewRegistry.addView({
  name: "instances/jklee",
  parent: "instances",
  path: "jklee",
  component: jkleeEndpoint,
  label: "jklee",
  isEnabled: ({ instance }) => {
    return instance.hasEndpoint("jklee-settings");
  },
})
