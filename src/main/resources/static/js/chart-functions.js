// work tab
export function workFilter(filter) {
    switch (filter) {
        case 'Volume':
            return preworkvolume;
        case 'Weight':
            return preworkweight;
        case 'Reps':
            return preworkreps;
        case 'Sets':
            return preworksets;
        default:
            return [];
    }
}