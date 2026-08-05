<script>
  export let variant = 'primary'; // 'primary' | 'secondary' | 'outline' | 'danger'
  export let type = 'button';
  export let disabled = false;
  export let ariaLabel = '';

  const baseStyles = 'inline-flex items-center justify-center font-label-caps text-label-caps uppercase tracking-wider rounded-lg px-lg py-sm transition-all duration-150 ease-in-out focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-fixed-dim focus-visible:ring-offset-2 focus-visible:ring-offset-background disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer active:scale-95';

  const variants = {
    primary: 'bg-primary text-on-primary hover:bg-primary-fixed hover:text-on-primary-fixed border border-primary/20',
    secondary: 'bg-secondary text-on-secondary hover:bg-secondary-fixed hover:text-on-secondary-fixed border border-secondary/20',
    outline: 'bg-transparent text-primary border border-primary/40 hover:bg-primary-container/10 hover:border-primary',
    danger: 'bg-error-container text-error hover:bg-on-error hover:text-on-error-container border border-error/20'
  };
</script>

<button
  {type}
  {disabled}
  aria-label={ariaLabel || undefined}
  class="{baseStyles} {variants[variant] || variants.primary}"
  on:click
  on:keydown
>
  <slot />
</button>
