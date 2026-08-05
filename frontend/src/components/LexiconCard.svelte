<script>
  export let title = '';
  export let subtitle = '';
  export let interactive = false;
  export let ariaLabel = '';

  const cardClass = `flex flex-col bg-surface-container rounded-xl ghost-border p-lg transition-all duration-300 ${interactive ? 'hover:bg-surface-container-high hover:border-primary/30 cursor-pointer focus-within:ring-2 focus-within:ring-primary-fixed-dim' : ''}`;
</script>

<article
  class={cardClass}
  aria-label={ariaLabel || title || 'Content Card'}
>
  {#if title || subtitle}
    <header class="border-b border-outline-variant/30 pb-xs mb-sm shrink-0">
      {#if title}
        <h3 class="font-headline-lg-mobile text-headline-lg-mobile text-primary mb-xs">{title}</h3>
      {/if}
      {#if subtitle}
        <p class="font-body-sm text-body-sm text-on-surface-variant">{subtitle}</p>
      {/if}
    </header>
  {/if}

  <div class="flex-1 font-body-md text-body-md text-on-surface flex flex-col gap-sm">
    <slot />
  </div>

  {#if $$slots.footer}
    <footer class="mt-md pt-sm border-t border-outline-variant/20 shrink-0 flex items-center justify-end gap-sm">
      <slot name="footer" />
    </footer>
  {/if}
</article>
