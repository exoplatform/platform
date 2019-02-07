export function tooltip(el, binding) {
  const element = $(el);
  const placement = Object.keys(binding.modifiers)[0];
  const container = Object.keys(binding.modifiers)[1];
  element.attr('data-original-title', binding.value);
  if (placement) {
    element.attr('data-placement', placement);
  }
  if (container) {
    element.attr('data-container', container);
  }
  element.tooltip();
}