<script>
  import { createEventDispatcher } from 'svelte';

  export let value = '';
  export let placeholder = 'Search knowledge base...';
  export let id = 'search-input';
  export let ariaLabel = 'Search input';

  const dispatch = createEventDispatcher();

  function handleInput(event) {
    value = event.target.value;
    dispatch('input', value);
  }

  function handleKeyDown(event) {
    if (event.key === 'Enter') {
      dispatch('search', value);
    }
  }

  function handleClear() {
    value = '';
    dispatch('input', value);
    dispatch('clear');
    document.getElementById(id)?.focus();
  }
</script>

<div class="relative w-full flex items-center bg-surface-container rounded-lg border border-outline-variant hover:border-outline focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-all duration-150 ease-in-out">
  <span class="material-symbols-outlined ml-md text-on-surface-variant select-none pointer-events-none">
    search
  </span>
  <input
    {id}
    type="text"
    {placeholder}
    {value}
    aria-label={ariaLabel}
    class="w-full bg-transparent border-0 ring-0 focus:ring-0 focus:outline-none py-sm px-md font-body-md text-body-md text-on-surface placeholder:text-on-surface-variant/50"
    on:input={handleInput}
    on:keydown={handleKeyDown}
  />
  {#if value}
    <button
      type="button"
      class="mr-md text-on-surface-variant hover:text-primary focus:text-primary transition-colors cursor-pointer focus:outline-none flex items-center justify-center rounded-full p-xs hover:bg-surface-variant/40"
      aria-label="Clear search query"
      on:click={handleClear}
    >
      <span class="material-symbols-outlined text-body-sm">
        close
      </span>
    </button>
  {/if}
</div>
