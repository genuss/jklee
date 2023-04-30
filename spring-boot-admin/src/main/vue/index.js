import jkleeEndpoint from './jklee';

SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'instances/jklee',
      parent: 'instances',
      path: 'jklee',
      component: jkleeEndpoint,
      label: 'jklee',
      isEnabled: ({instance}) => instance.hasEndpoint('jklee-settings'),
    });
  }
});
